import {
  Component, OnDestroy, AfterViewChecked,
  ViewChild, ElementRef
} from '@angular/core';
import { NgFor, NgIf, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Client, IMessage } from '@stomp/stompjs';
import { environment } from '../../../environments/environment';

interface Msg {
  senderName: string;
  senderRole: string;
  content: string;
  sentAt: string;
}

@Component({
  selector: 'app-support-chat',
  standalone: true,
  imports: [NgFor, NgIf, DatePipe, FormsModule],
  template: `
    <div class="support-page">

      <!-- BRAND HEADER -->
      <div class="support-nav">
        <div class="support-nav__logo">
          <div class="support-nav__icon">FT</div>
          <span class="support-nav__name">Fast Trade</span>
        </div>
        <span class="support-nav__tag">Central de Suporte</span>
      </div>

      <!-- STEP 1: FORM -->
      <div class="support-container" *ngIf="step === 'form'">
        <div class="support-card">
          <div class="support-card__icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>
            </svg>
          </div>
          <h1 class="support-card__title">Fale conosco</h1>
          <p class="support-card__sub">Nossa equipe responde em minutos. Preencha seus dados para começar.</p>

          <div class="form-group">
            <label class="form-label">Seu nome *</label>
            <input class="form-input" type="text" [(ngModel)]="form.name" placeholder="Ex: João Silva" />
          </div>
          <div class="form-group">
            <label class="form-label">E-mail *</label>
            <input class="form-input" type="email" [(ngModel)]="form.email" placeholder="seu@email.com" />
          </div>
          <div class="form-group">
            <label class="form-label">Como podemos ajudar?</label>
            <textarea class="form-input form-textarea" [(ngModel)]="form.message" rows="3" placeholder="Descreva brevemente o que precisa..."></textarea>
          </div>

          <button class="form-submit" [disabled]="!form.name.trim() || !form.email.trim() || starting" (click)="startChat()">
            <span *ngIf="!starting">Iniciar conversa</span>
            <span *ngIf="starting" class="btn-loading">
              <span class="spinner"></span> Conectando…
            </span>
          </button>

          <p class="form-disclaimer">Seus dados são usados apenas para identificação no atendimento.</p>
        </div>
      </div>

      <!-- STEP 2: CHAT -->
      <div class="chat-container" *ngIf="step === 'chat'">

        <!-- Chat header -->
        <div class="chat-head">
          <div class="chat-head__avatar">FT</div>
          <div>
            <div class="chat-head__name">Suporte Fast Trade</div>
            <div class="chat-head__status">
              <span class="status-dot" [class.status-dot--on]="connected"></span>
              {{ connected ? 'Online agora' : 'Reconectando…' }}
            </div>
          </div>
        </div>

        <!-- Messages -->
        <div class="chat-messages" #msgBox>
          <!-- Welcome message -->
          <div class="msg-row">
            <div class="msg-bubble msg-bubble--admin">
              <div class="msg-text">Olá, <strong>{{ form.name }}</strong>! 👋 Nossa equipe recebeu seu contato e responderá em breve.</div>
              <div class="msg-time">agora</div>
            </div>
          </div>

          <ng-container *ngFor="let m of messages">
            <div *ngIf="m.senderRole === 'SYSTEM'" class="sys-msg">{{ m.content }}</div>
            <div *ngIf="m.senderRole !== 'SYSTEM'" class="msg-row" [class.msg-row--mine]="isCustomer(m)">
              <div class="msg-bubble" [class.msg-bubble--mine]="isCustomer(m)" [class.msg-bubble--admin]="!isCustomer(m)">
                <div class="msg-sender" *ngIf="!isCustomer(m)">{{ m.senderName }}</div>
                <div class="msg-text">{{ m.content }}</div>
                <div class="msg-time">{{ m.sentAt | date:'HH:mm' }}</div>
              </div>
            </div>
          </ng-container>

          <div *ngIf="adminTyping" class="msg-row">
            <div class="msg-bubble msg-bubble--admin msg-bubble--typing">
              <span class="dot"></span><span class="dot"></span><span class="dot"></span>
            </div>
          </div>
        </div>

        <!-- Input -->
        <div class="chat-input-bar">
          <div class="chat-input-wrap">
            <textarea class="chat-input" [(ngModel)]="draft" placeholder="Digite sua mensagem…"
              rows="1" (keydown.enter)="onEnter($any($event))" [disabled]="!connected"></textarea>
            <button class="chat-send" (click)="send()" [disabled]="!draft.trim() || !connected">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
            </button>
          </div>
          <div class="chat-hint">Enter para enviar · Shift+Enter para nova linha</div>
        </div>
      </div>

    </div>`,
  styles: [`
    .support-page { min-height: 100vh; background: #f0f4ff; display: flex; flex-direction: column; font-family: inherit; }

    /* Nav */
    .support-nav { display: flex; align-items: center; justify-content: space-between; padding: 16px 32px; background: #fff; border-bottom: 1px solid #e5e7eb; }
    .support-nav__logo { display: flex; align-items: center; gap: 10px; }
    .support-nav__icon { width: 36px; height: 36px; border-radius: 10px; background: #2563eb; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 900; font-size: 13px; }
    .support-nav__name { font-size: 17px; font-weight: 800; color: #111827; }
    .support-nav__tag { font-size: 12px; font-weight: 600; color: #6b7280; background: #f3f4f6; padding: 4px 12px; border-radius: 999px; }

    /* Form step */
    .support-container { flex: 1; display: flex; align-items: center; justify-content: center; padding: 40px 16px; }
    .support-card { background: #fff; border-radius: 20px; padding: 40px 36px; width: 100%; max-width: 440px; box-shadow: 0 8px 40px rgba(0,0,0,.1); }
    .support-card__icon { width: 60px; height: 60px; border-radius: 16px; background: #eff6ff; color: #2563eb; display: flex; align-items: center; justify-content: center; margin-bottom: 20px; }
    .support-card__title { font-size: 24px; font-weight: 900; color: #111827; margin: 0 0 8px; }
    .support-card__sub { font-size: 14px; color: #6b7280; margin: 0 0 28px; line-height: 1.5; }

    .form-group { margin-bottom: 18px; }
    .form-label { display: block; font-size: 12px; font-weight: 700; color: #374151; text-transform: uppercase; letter-spacing: .4px; margin-bottom: 6px; }
    .form-input { width: 100%; border: 1.5px solid #e5e7eb; border-radius: 10px; padding: 10px 14px; font-size: 14px; color: #111827; font-family: inherit; outline: none; transition: border-color .15s; box-sizing: border-box; }
    .form-input:focus { border-color: #2563eb; box-shadow: 0 0 0 3px rgba(37,99,235,.1); }
    .form-textarea { resize: vertical; min-height: 80px; }
    .form-submit { width: 100%; background: #2563eb; color: #fff; border: none; border-radius: 12px; padding: 14px; font-size: 15px; font-weight: 700; cursor: pointer; margin-top: 8px; transition: background .15s; display: flex; align-items: center; justify-content: center; gap: 8px; }
    .form-submit:hover:not(:disabled) { background: #1d4ed8; }
    .form-submit:disabled { opacity: .5; cursor: not-allowed; }
    .btn-loading { display: flex; align-items: center; gap: 8px; }
    .spinner { width: 16px; height: 16px; border: 2px solid rgba(255,255,255,.4); border-top-color: #fff; border-radius: 50%; animation: spin .7s linear infinite; }
    @keyframes spin { to { transform: rotate(360deg); } }
    .form-disclaimer { font-size: 11px; color: #9ca3af; text-align: center; margin-top: 14px; }

    /* Chat step */
    .chat-container { flex: 1; display: flex; flex-direction: column; max-width: 680px; width: 100%; margin: 24px auto; background: #fff; border-radius: 20px; box-shadow: 0 8px 40px rgba(0,0,0,.1); overflow: hidden; }

    .chat-head { display: flex; align-items: center; gap: 14px; padding: 16px 20px; background: #2563eb; color: #fff; }
    .chat-head__avatar { width: 42px; height: 42px; border-radius: 50%; background: rgba(255,255,255,.25); display: flex; align-items: center; justify-content: center; font-weight: 900; font-size: 13px; flex-shrink: 0; }
    .chat-head__name { font-size: 15px; font-weight: 700; }
    .chat-head__status { display: flex; align-items: center; gap: 6px; font-size: 12px; opacity: .85; margin-top: 2px; }
    .status-dot { width: 7px; height: 7px; border-radius: 50%; background: #94a3b8; }
    .status-dot--on { background: #4ade80; }

    .chat-messages { flex: 1; overflow-y: auto; padding: 20px; display: flex; flex-direction: column; gap: 8px; background: #f8fafc; }
    .sys-msg { text-align: center; font-size: 11px; color: #94a3b8; background: #f1f5f9; padding: 4px 14px; border-radius: 999px; align-self: center; }

    .msg-row { display: flex; max-width: 80%; }
    .msg-row--mine { align-self: flex-end; }
    .msg-bubble { border-radius: 16px 16px 16px 4px; padding: 10px 14px; max-width: 100%; background: #fff; border: 1px solid #e5e7eb; }
    .msg-bubble--mine { background: #2563eb; border-color: transparent; border-radius: 16px 16px 4px 16px; }
    .msg-bubble--admin { background: #fff; }
    .msg-sender { font-size: 11px; font-weight: 700; color: #6b7280; margin-bottom: 3px; }
    .msg-text { font-size: 14px; line-height: 1.5; color: #111827; }
    .msg-bubble--mine .msg-text { color: #fff; }
    .msg-time { font-size: 10px; margin-top: 4px; text-align: right; opacity: .5; color: #374151; }
    .msg-bubble--mine .msg-time { color: #fff; opacity: .7; }
    .msg-bubble--typing { display: flex; gap: 4px; align-items: center; padding: 12px 16px; }
    .dot { width: 7px; height: 7px; border-radius: 50%; background: #94a3b8; animation: bounce .9s infinite; }
    .dot:nth-child(2) { animation-delay: .15s; }
    .dot:nth-child(3) { animation-delay: .3s; }
    @keyframes bounce { 0%,60%,100% { transform: translateY(0); } 30% { transform: translateY(-5px); } }

    .chat-input-bar { padding: 12px 16px; border-top: 1px solid #e5e7eb; background: #fff; }
    .chat-input-wrap { display: flex; align-items: flex-end; gap: 8px; background: #f9fafb; border: 1.5px solid #e5e7eb; border-radius: 12px; padding: 8px 8px 8px 14px; transition: border-color .15s; }
    .chat-input-wrap:focus-within { border-color: #2563eb; box-shadow: 0 0 0 3px rgba(37,99,235,.1); }
    .chat-input { flex: 1; border: none; background: none; outline: none; font-size: 14px; color: #111827; resize: none; max-height: 120px; font-family: inherit; line-height: 1.5; }
    .chat-send { width: 36px; height: 36px; border-radius: 10px; background: #2563eb; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; color: #fff; flex-shrink: 0; transition: background .15s; }
    .chat-send:hover:not(:disabled) { background: #1d4ed8; }
    .chat-send:disabled { opacity: .4; cursor: not-allowed; }
    .chat-hint { font-size: 10px; color: #9ca3af; margin-top: 5px; padding-left: 2px; }
  `]
})
export class SupportChatComponent implements OnDestroy, AfterViewChecked {
  @ViewChild('msgBox') msgBox!: ElementRef<HTMLDivElement>;

  private http = new HttpClient(undefined as any); // injected via DI below
  private client!: Client;
  private baseUrl = environment.apiUrl;
  private wsUrl = `${this.baseUrl.replace('http', 'ws').replace('/api', '')}/ws/websocket`;

  step: 'form' | 'chat' = 'form';
  connected = false;
  starting = false;
  adminTyping = false;
  typingTimer: any;
  shouldScroll = true;

  form = { name: '', email: '', message: '' };
  draft = '';
  room = '';
  messages: Msg[] = [];

  constructor(private httpClient: HttpClient) {}

  async startChat(): Promise<void> {
    if (!this.form.name.trim() || !this.form.email.trim()) return;
    this.starting = true;

    try {
      const res = await this.httpClient.post<{ room: string }>(
        `${this.baseUrl}/chat/support/request`,
        { name: this.form.name, email: this.form.email }
      ).toPromise();

      this.room = res!.room;

      // Load history
      const history = await this.httpClient.get<Msg[]>(
        `${this.baseUrl}/chat/history/${this.room}`
      ).toPromise();
      this.messages = history ?? [];

      this.connectWebSocket();
      this.step = 'chat';

      // Send initial message if provided
      if (this.form.message.trim()) {
        setTimeout(() => this.sendRaw(this.form.message.trim()), 1000);
      }
    } catch {
      alert('Erro ao conectar. Tente novamente.');
    } finally {
      this.starting = false;
    }
  }

  private connectWebSocket(): void {
    this.client = new Client({
      brokerURL: this.wsUrl,
      reconnectDelay: 3000,
      onConnect: () => {
        this.connected = true;
        // Subscribe to room
        this.client.subscribe(`/topic/${this.room}`, (msg: IMessage) => {
          const m: Msg = JSON.parse(msg.body);
          if (!this.messages.find(x => x.sentAt === m.sentAt && x.content === m.content)) {
            this.messages.push(m);
            this.shouldScroll = true;
          }
        });
        // Subscribe to typing
        this.client.subscribe(`/topic/${this.room}.typing`, (msg: IMessage) => {
          const ev = JSON.parse(msg.body);
          if (ev.typing === 'true' && ev.user !== this.form.email) {
            this.adminTyping = true;
            clearTimeout(this.typingTimer);
            this.typingTimer = setTimeout(() => this.adminTyping = false, 3000);
          } else if (ev.typing === 'false') {
            this.adminTyping = false;
          }
        });
      },
      onDisconnect: () => { this.connected = false; },
    });
    this.client.activate();
  }

  send(): void {
    if (!this.draft.trim() || !this.connected) return;
    this.sendRaw(this.draft.trim());
    this.draft = '';
  }

  private sendRaw(content: string): void {
    if (!this.client?.active) return;
    this.client.publish({
      destination: '/app/chat.send',
      body: JSON.stringify({ room: this.room, content, senderName: this.form.name }),
    });
  }

  onEnter(e: KeyboardEvent): void {
    if (!e.shiftKey) { e.preventDefault(); this.send(); }
  }

  isCustomer(m: Msg): boolean {
    return m.senderRole === 'CUSTOMER';
  }

  ngAfterViewChecked(): void {
    if (this.shouldScroll) {
      try { this.msgBox?.nativeElement && (this.msgBox.nativeElement.scrollTop = this.msgBox.nativeElement.scrollHeight); } catch {}
      this.shouldScroll = false;
    }
  }

  ngOnDestroy(): void {
    this.client?.deactivate();
  }
}
