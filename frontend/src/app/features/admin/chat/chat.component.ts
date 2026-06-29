import {
  Component, OnInit, OnDestroy, AfterViewChecked,
  ViewChild, ElementRef, inject
} from '@angular/core';
import { NgFor, NgIf, DatePipe, CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ChatService, ChatMessage, RoomSummary } from '../../../core/services/chat.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [NgFor, NgIf, CommonModule, DatePipe, FormsModule],
  template: `
    <div class="chat-page">

      <!-- LEFT PANEL -->
      <div class="chat-sidebar">
        <div class="chat-sidebar__head">
          <h2 class="chat-sidebar__title">Chat</h2>
          <div class="conn-dot" [class.conn-dot--on]="connected" [title]="connected ? 'Conectado' : 'Reconectando…'"></div>
        </div>

        <!-- Tab switcher -->
        <div class="chat-tabs">
          <button class="chat-tab" [class.chat-tab--active]="activeTab === 'internal'" (click)="switchTab('internal')">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></svg>
            Equipe
          </button>
          <button class="chat-tab" [class.chat-tab--active]="activeTab === 'support'" (click)="switchTab('support')">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/></svg>
            Suporte
            <span class="chat-tab__badge" *ngIf="unread.support > 0">{{ unread.support }}</span>
          </button>
        </div>

        <!-- Internal channel -->
        <div *ngIf="activeTab === 'internal'" class="chat-room-item chat-room-item--active">
          <div class="chat-room-item__avatar chat-room-item__avatar--team">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
          </div>
          <div class="chat-room-item__info">
            <span class="chat-room-item__name">Canal Interno</span>
            <span class="chat-room-item__sub">Toda a equipe</span>
          </div>
        </div>

        <!-- Support: filter + room list -->
        <div *ngIf="activeTab === 'support'" class="support-panel">

          <!-- Filter pills -->
          <div class="support-filters">
            <button *ngFor="let f of filters" class="support-filter"
              [class.support-filter--active]="supportFilter === f.key"
              (click)="supportFilter = f.key">
              {{ f.label }}
              <span *ngIf="f.key === 'WAITING' && waitingCount > 0" class="support-filter__dot"></span>
            </button>
          </div>

          <!-- Room cards -->
          <div class="chat-rooms-list">
            <ng-container *ngIf="filteredRooms.length > 0; else emptyRooms">
              <div *ngFor="let r of filteredRooms"
                class="room-card"
                [class.room-card--active]="selectedRoom === r.room"
                (click)="openRoom(r.room)">

                <div class="room-card__avatar" [style.background]="avatarBg(r.userName)">
                  {{ r.userName?.charAt(0)?.toUpperCase() || 'C' }}
                </div>

                <div class="room-card__body">
                  <div class="room-card__top">
                    <span class="room-card__name">{{ r.userName }}</span>
                    <span class="room-card__time">{{ r.lastMessageAt | date:'HH:mm' }}</span>
                  </div>
                  <div class="room-card__bottom">
                    <span class="room-card__preview">{{ r.lastMessage || 'Sem mensagens' }}</span>
                    <span class="room-card__unread" *ngIf="r.unreadCount > 0">{{ r.unreadCount }}</span>
                  </div>
                  <span class="room-card__status" [class]="'room-card__status--' + r.status.toLowerCase()">
                    {{ statusLabel(r.status) }}
                  </span>
                </div>
              </div>
            </ng-container>
            <ng-template #emptyRooms>
              <div class="chat-rooms-empty">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/></svg>
                <p>Nenhuma conversa ainda</p>
              </div>
            </ng-template>
          </div>
        </div>
      </div>

      <!-- MAIN CHAT AREA -->
      <div class="chat-main">

        <!-- Header -->
        <div class="chat-header">
          <div class="chat-header__info">
            <div class="chat-header__avatar" [class.chat-header__avatar--team]="activeTab === 'internal'">
              <svg *ngIf="activeTab === 'internal'" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
              <span *ngIf="activeTab === 'support'">{{ selectedRoomSummary?.userName?.charAt(0)?.toUpperCase() || 'C' }}</span>
            </div>
            <div>
              <div class="chat-header__name">
                {{ activeTab === 'internal' ? 'Canal Interno da Equipe' : (selectedRoomSummary?.userName || 'Cliente') }}
              </div>
              <div class="chat-header__sub" *ngIf="typingUser">
                <span class="typing-dots"><span></span><span></span><span></span></span>
                {{ typingUser }} está digitando…
              </div>
              <div class="chat-header__sub" *ngIf="!typingUser">
                <span *ngIf="activeTab === 'internal'">Chat privado entre colaboradores</span>
                <span *ngIf="activeTab === 'support' && selectedRoomSummary">
                  {{ selectedRoomSummary.userEmail }}
                  &nbsp;·&nbsp;
                  <span class="header-status" [class]="'header-status--' + selectedRoomSummary.status.toLowerCase()">
                    {{ statusLabel(selectedRoomSummary.status) }}
                  </span>
                </span>
              </div>
            </div>
          </div>

          <!-- New support notification toast -->
          <div class="new-request-toast" *ngIf="newRequestNotif" (click)="dismissNotif()">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 16.92v3a2 2 0 01-2.18 2 19.79 19.79 0 01-8.63-3.07A19.5 19.5 0 013.07 9.81 19.79 19.79 0 01.06 1.2 2 2 0 012.03 0h3a2 2 0 012 1.72c.127.96.361 1.903.7 2.81a2 2 0 01-.45 2.11L6.09 7.91a16 16 0 006 6l1.27-1.27a2 2 0 012.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0122 14.92z"/></svg>
            Nova solicitação: <strong>{{ newRequestNotif.senderName }}</strong>
          </div>
        </div>

        <!-- Messages -->
        <div class="chat-messages" #msgContainer>
          <div *ngIf="currentMessages.length === 0" class="chat-messages__empty">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1"><path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/></svg>
            <p>Nenhuma mensagem ainda.<br>Seja o primeiro a enviar!</p>
          </div>

          <ng-container *ngFor="let msg of currentMessages; let i = index">
            <div class="date-sep" *ngIf="showDateSep(msg, currentMessages[i-1])">
              {{ msg.sentAt | date:'dd/MM/yyyy' }}
            </div>

            <!-- System message -->
            <div *ngIf="msg.senderRole === 'SYSTEM'" class="system-msg">
              <span>{{ msg.content }}</span>
            </div>

            <!-- Regular message -->
            <div *ngIf="msg.senderRole !== 'SYSTEM'" class="msg-row" [class.msg-row--mine]="isMine(msg)">
              <div class="msg-avatar" *ngIf="!isMine(msg)" [class.msg-avatar--customer]="msg.senderRole === 'CUSTOMER'">
                {{ msg.senderName?.charAt(0) || 'U' }}
              </div>
              <div class="msg-bubble" [class.msg-bubble--mine]="isMine(msg)" [class.msg-bubble--customer]="msg.senderRole === 'CUSTOMER' && !isMine(msg)">
                <div class="msg-sender" *ngIf="!isMine(msg)">
                  {{ msg.senderName }}
                  <span class="msg-role" [class]="'msg-role--' + (msg.senderRole || '').toLowerCase()">{{ roleLabel(msg.senderRole) }}</span>
                </div>
                <div class="msg-text">{{ msg.content }}</div>
                <div class="msg-time">{{ msg.sentAt | date:'HH:mm' }}</div>
              </div>
            </div>
          </ng-container>
        </div>

        <!-- Input -->
        <div class="chat-input-bar">
          <div class="chat-input-wrap">
            <textarea
              #inputEl
              class="chat-input"
              [(ngModel)]="draft"
              placeholder="Digite uma mensagem…"
              rows="1"
              (keydown.enter)="onEnter($any($event))"
              (input)="onTyping()"
              (blur)="stopTyping()">
            </textarea>
            <button class="chat-send" [disabled]="!draft.trim() || !connected" (click)="send()">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
            </button>
          </div>
          <div class="chat-hint">Enter para enviar · Shift+Enter para nova linha</div>
        </div>
      </div>
    </div>`,
  styles: [`
    .chat-page { display: flex; height: calc(100vh - 64px); background: var(--color-bg); border-radius: var(--radius-lg); overflow: hidden; box-shadow: var(--shadow-sm); border: 1.5px solid var(--color-border); }

    /* Sidebar */
    .chat-sidebar { width: 280px; flex-shrink: 0; background: var(--color-surface); border-right: 1px solid var(--color-border); display: flex; flex-direction: column; }
    .chat-sidebar__head { display: flex; align-items: center; justify-content: space-between; padding: 20px 18px 12px; }
    .chat-sidebar__title { font-size: 16px; font-weight: 800; color: var(--color-text); }
    .conn-dot { width: 8px; height: 8px; border-radius: 50%; background: #94a3b8; }
    .conn-dot--on { background: #22c55e; box-shadow: 0 0 0 3px rgba(34,197,94,.2); animation: pulse 2s infinite; }
    @keyframes pulse { 0%,100% { box-shadow: 0 0 0 3px rgba(34,197,94,.2); } 50% { box-shadow: 0 0 0 5px rgba(34,197,94,.08); } }

    .chat-tabs { display: flex; gap: 2px; padding: 0 10px 10px; }
    .chat-tab { flex: 1; display: flex; align-items: center; justify-content: center; gap: 5px; padding: 7px 8px; font-size: 12px; font-weight: 600; border: none; border-radius: var(--radius-md); background: none; cursor: pointer; color: var(--color-text-muted); transition: all .15s; position: relative; }
    .chat-tab:hover { background: var(--color-bg); color: var(--color-text); }
    .chat-tab--active { background: #eff6ff; color: var(--color-accent); }
    .chat-tab__badge { background: #ef4444; color: #fff; border-radius: 20px; padding: 1px 6px; font-size: 10px; font-weight: 800; }

    .chat-room-item { display: flex; align-items: center; gap: 10px; padding: 11px 14px; cursor: pointer; border-radius: var(--radius-md); margin: 0 6px 2px; }
    .chat-room-item:hover { background: var(--color-bg); }
    .chat-room-item--active { background: #eff6ff; }
    .chat-room-item__avatar { width: 36px; height: 36px; border-radius: 50%; background: #7c3aed; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 700; flex-shrink: 0; }
    .chat-room-item__avatar--team { background: #7c3aed; }
    .chat-room-item__info { display: flex; flex-direction: column; gap: 1px; }
    .chat-room-item__name { font-size: 13px; font-weight: 600; color: var(--color-text); }
    .chat-room-item__sub { font-size: 11px; color: var(--color-text-muted); }

    /* Support panel */
    .support-panel { display: flex; flex-direction: column; flex: 1; min-height: 0; }

    .support-filters { display: flex; gap: 6px; padding: 4px 12px 10px; }
    .support-filter { font-size: 11px; font-weight: 700; padding: 4px 12px; border-radius: 999px; border: 1.5px solid var(--color-border); background: none; cursor: pointer; color: var(--color-text-muted); transition: all .15s; position: relative; display: flex; align-items: center; gap: 4px; }
    .support-filter:hover { background: var(--color-bg); }
    .support-filter--active { background: #eff6ff; border-color: var(--color-accent); color: var(--color-accent); }
    .support-filter__dot { width: 6px; height: 6px; border-radius: 50%; background: #ef4444; }

    .chat-rooms-list { flex: 1; overflow-y: auto; padding: 0 6px 6px; }
    .chat-rooms-empty { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 10px; padding: 40px 20px; color: var(--color-text-muted); text-align: center; font-size: 13px; }

    /* Room cards */
    .room-card { display: flex; align-items: flex-start; gap: 10px; padding: 10px 10px; cursor: pointer; border-radius: var(--radius-md); transition: background .12s; margin-bottom: 2px; }
    .room-card:hover { background: var(--color-bg); }
    .room-card--active { background: #eff6ff; }
    .room-card__avatar { width: 38px; height: 38px; border-radius: 50%; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 14px; font-weight: 800; flex-shrink: 0; }
    .room-card__body { flex: 1; min-width: 0; }
    .room-card__top { display: flex; justify-content: space-between; align-items: center; gap: 4px; }
    .room-card__name { font-size: 13px; font-weight: 700; color: var(--color-text); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .room-card__time { font-size: 10px; color: var(--color-text-muted); flex-shrink: 0; }
    .room-card__bottom { display: flex; align-items: center; justify-content: space-between; gap: 6px; margin-top: 2px; }
    .room-card__preview { font-size: 11px; color: var(--color-text-muted); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; flex: 1; }
    .room-card__unread { background: var(--color-accent); color: #fff; border-radius: 999px; font-size: 10px; font-weight: 800; padding: 1px 6px; flex-shrink: 0; min-width: 18px; text-align: center; }
    .room-card__status { display: inline-block; margin-top: 4px; font-size: 10px; font-weight: 700; padding: 2px 8px; border-radius: 999px; letter-spacing: .3px; }
    .room-card__status--waiting { background: #fef9c3; color: #ca8a04; }
    .room-card__status--replied  { background: #dcfce7; color: #16a34a; }
    .room-card__status--new      { background: #eff6ff; color: #2563eb; }

    /* Main chat */
    .chat-main { flex: 1; display: flex; flex-direction: column; min-width: 0; }

    .chat-header { padding: 14px 20px; border-bottom: 1px solid var(--color-border); background: var(--color-surface); display: flex; align-items: center; justify-content: space-between; flex-shrink: 0; gap: 12px; }
    .chat-header__info { display: flex; align-items: center; gap: 12px; flex: 1; min-width: 0; }
    .chat-header__avatar { width: 40px; height: 40px; border-radius: 50%; background: var(--color-primary); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 15px; font-weight: 700; flex-shrink: 0; }
    .chat-header__avatar--team { background: #7c3aed; }
    .chat-header__name { font-size: 14px; font-weight: 700; color: var(--color-text); }
    .chat-header__sub { font-size: 12px; color: var(--color-text-muted); display: flex; align-items: center; gap: 5px; margin-top: 1px; }
    .header-status { font-weight: 700; font-size: 11px; }
    .header-status--waiting { color: #ca8a04; }
    .header-status--replied  { color: #16a34a; }
    .header-status--new      { color: #2563eb; }

    /* New request toast */
    .new-request-toast { display: flex; align-items: center; gap: 8px; background: #eff6ff; border: 1.5px solid #bfdbfe; border-radius: 10px; padding: 8px 14px; font-size: 12px; color: #1d4ed8; cursor: pointer; animation: slideIn .3s ease; flex-shrink: 0; }
    @keyframes slideIn { from { opacity:0; transform:translateY(-8px); } to { opacity:1; transform:translateY(0); } }

    /* Typing dots */
    .typing-dots { display: inline-flex; gap: 2px; align-items: center; }
    .typing-dots span { width: 4px; height: 4px; border-radius: 50%; background: var(--color-text-muted); animation: bounce 1s infinite; }
    .typing-dots span:nth-child(2) { animation-delay: .15s; }
    .typing-dots span:nth-child(3) { animation-delay: .3s; }
    @keyframes bounce { 0%,60%,100% { transform:translateY(0); } 30% { transform:translateY(-4px); } }

    /* Messages */
    .chat-messages { flex: 1; overflow-y: auto; padding: 20px; display: flex; flex-direction: column; gap: 4px; }
    .chat-messages__empty { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 12px; flex: 1; color: var(--color-text-muted); text-align: center; font-size: 13px; line-height: 1.5; }
    .date-sep { text-align: center; font-size: 11px; color: var(--color-text-muted); margin: 12px 0; display: flex; align-items: center; gap: 10px; }
    .date-sep::before, .date-sep::after { content: ''; flex: 1; height: 1px; background: var(--color-border); }
    .system-msg { text-align: center; font-size: 11px; color: var(--color-text-muted); padding: 4px 16px; background: var(--color-bg); border-radius: 999px; align-self: center; margin: 4px 0; }

    .msg-row { display: flex; gap: 8px; max-width: 75%; margin-bottom: 2px; }
    .msg-row--mine { align-self: flex-end; flex-direction: row-reverse; }
    .msg-avatar { width: 30px; height: 30px; border-radius: 50%; background: var(--color-primary); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 700; flex-shrink: 0; align-self: flex-end; }
    .msg-avatar--customer { background: #ea580c; }
    .msg-bubble { background: var(--color-surface); border: 1px solid var(--color-border); border-radius: 14px 14px 14px 4px; padding: 8px 12px; max-width: 100%; }
    .msg-bubble--mine { background: var(--color-accent); border-color: transparent; border-radius: 14px 14px 4px 14px; }
    .msg-bubble--customer { background: #fff7ed; border-color: #fed7aa; }
    .msg-sender { font-size: 11px; font-weight: 700; color: var(--color-text-muted); margin-bottom: 2px; display: flex; align-items: center; gap: 6px; }
    .msg-role { padding: 1px 6px; border-radius: 10px; font-size: 9px; font-weight: 700; text-transform: uppercase; }
    .msg-role--admin    { background: #fef2f2; color: #dc2626; }
    .msg-role--manager  { background: #eff6ff; color: #2563eb; }
    .msg-role--support  { background: #f0fdf4; color: #16a34a; }
    .msg-role--customer { background: #fff7ed; color: #ea580c; }
    .msg-text { font-size: 13px; line-height: 1.5; word-break: break-word; white-space: pre-wrap; }
    .msg-bubble--mine .msg-text { color: #fff; }
    .msg-time { font-size: 10px; margin-top: 4px; text-align: right; opacity: .6; }

    /* Input */
    .chat-input-bar { padding: 12px 16px; border-top: 1px solid var(--color-border); background: var(--color-surface); flex-shrink: 0; }
    .chat-input-wrap { display: flex; align-items: flex-end; gap: 8px; background: var(--color-bg); border: 1.5px solid var(--color-border); border-radius: 12px; padding: 8px 8px 8px 14px; transition: border-color .15s; }
    .chat-input-wrap:focus-within { border-color: var(--color-accent); box-shadow: 0 0 0 3px rgba(37,99,235,.1); }
    .chat-input { flex: 1; border: none; background: none; outline: none; font-size: 13px; color: var(--color-text); resize: none; max-height: 120px; font-family: inherit; line-height: 1.5; }
    .chat-send { width: 34px; height: 34px; border-radius: 8px; background: var(--color-accent); border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; color: #fff; flex-shrink: 0; transition: all .15s; }
    .chat-send:hover:not(:disabled) { background: var(--color-primary); }
    .chat-send:disabled { opacity: .4; cursor: not-allowed; }
    .chat-hint { font-size: 10px; color: var(--color-text-muted); margin-top: 5px; padding-left: 2px; }
  `]
})
export class ChatComponent implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('msgContainer') msgContainer!: ElementRef<HTMLDivElement>;

  private chatSvc = inject(ChatService);
  private authSvc = inject(AuthService);
  private subs: Subscription[] = [];

  connected = false;
  activeTab: 'internal' | 'support' = 'internal';
  selectedRoom = 'internal';
  draft = '';
  typingUser = '';
  typingTimer: any;
  notifTimer: any;
  shouldScroll = true;

  currentMessages: ChatMessage[] = [];
  roomSummaries: RoomSummary[] = [];
  supportFilter: 'ALL' | 'WAITING' | 'REPLIED' = 'ALL';
  newRequestNotif: { senderName: string; room: string } | null = null;
  unread = { internal: 0, support: 0, total: 0 };

  filters = [
    { key: 'ALL' as const,     label: 'Todos' },
    { key: 'WAITING' as const, label: 'Aguardando' },
    { key: 'REPLIED' as const, label: 'Respondidos' },
  ];

  get filteredRooms(): RoomSummary[] {
    if (this.supportFilter === 'ALL') return this.roomSummaries;
    return this.roomSummaries.filter(r => r.status === this.supportFilter);
  }

  get waitingCount(): number {
    return this.roomSummaries.filter(r => r.status === 'WAITING').length;
  }

  get selectedRoomSummary(): RoomSummary | undefined {
    return this.roomSummaries.find(r => r.room === this.selectedRoom);
  }

  readonly AVATAR_COLORS = ['#2563eb','#7c3aed','#ea580c','#16a34a','#0891b2','#be185d','#d97706'];
  avatarBg(name: string): string {
    if (!name) return this.AVATAR_COLORS[0];
    return this.AVATAR_COLORS[name.charCodeAt(0) % this.AVATAR_COLORS.length];
  }

  ngOnInit(): void {
    this.chatSvc.connect();

    this.subs.push(
      this.chatSvc.isConnected$.subscribe(c => {
        this.connected = c;
        if (c) {
          this.chatSvc.loadHistory('internal');
          this.chatSvc.loadRoomsDetails();
          this.chatSvc.loadSupportRooms().then(rooms => {
            rooms.forEach(r => this.chatSvc.subscribeSupport(r));
          });
        }
      }),
      this.chatSvc.internalMessages.subscribe(msgs => {
        if (this.activeTab === 'internal') { this.currentMessages = msgs; this.shouldScroll = true; }
      }),
      this.chatSvc.supportMessages.subscribe(map => {
        if (this.activeTab === 'support' && this.selectedRoom !== 'internal') {
          this.currentMessages = map[this.selectedRoom] ?? [];
          this.shouldScroll = true;
        }
      }),
      this.chatSvc.roomSummaries.subscribe(r => this.roomSummaries = r),
      this.chatSvc.newSupportRequest.subscribe(req => {
        this.newRequestNotif = req;
        clearTimeout(this.notifTimer);
        this.notifTimer = setTimeout(() => this.newRequestNotif = null, 6000);
        // Subscribe to the new room
        this.chatSvc.subscribeSupport(req.room);
        this.chatSvc.loadRoomsDetails();
      }),
      this.chatSvc.typing.subscribe(e => {
        if (e.room === this.selectedRoom && e.typing === 'true') {
          this.typingUser = e.user;
          clearTimeout(this.typingTimer);
          this.typingTimer = setTimeout(() => { this.typingUser = ''; }, 3000);
        }
      }),
      this.chatSvc.unread.subscribe(u => this.unread = u),
    );
  }

  ngAfterViewChecked(): void {
    if (this.shouldScroll) { this.scrollToBottom(); this.shouldScroll = false; }
  }

  ngOnDestroy(): void {
    this.subs.forEach(s => s.unsubscribe());
    this.chatSvc.disconnect();
  }

  switchTab(tab: 'internal' | 'support'): void {
    this.activeTab = tab;
    if (tab === 'internal') {
      this.selectedRoom = 'internal';
      this.currentMessages = [];
      this.chatSvc.loadHistory('internal');
      this.chatSvc.markRead('internal');
    } else {
      this.currentMessages = [];
      if (this.roomSummaries.length > 0) this.openRoom(this.roomSummaries[0].room);
    }
  }

  openRoom(room: string): void {
    this.selectedRoom = room;
    this.chatSvc.loadHistory(room);
    this.chatSvc.subscribeSupport(room);
    this.chatSvc.markRead(room);
    this.shouldScroll = true;
  }

  dismissNotif(): void {
    if (this.newRequestNotif) this.openRoom(this.newRequestNotif.room);
    this.newRequestNotif = null;
    this.activeTab = 'support';
  }

  send(): void {
    if (!this.draft.trim() || !this.connected) return;
    this.chatSvc.sendMessage(this.selectedRoom, this.draft.trim());
    this.draft = '';
    this.chatSvc.sendTyping(this.selectedRoom, false);
    setTimeout(() => this.chatSvc.loadRoomsDetails(), 500);
  }

  onEnter(e: KeyboardEvent): void {
    if (!e.shiftKey) { e.preventDefault(); this.send(); }
  }

  onTyping(): void {
    this.chatSvc.sendTyping(this.selectedRoom, true);
    clearTimeout(this.typingTimer);
    this.typingTimer = setTimeout(() => this.chatSvc.sendTyping(this.selectedRoom, false), 2000);
  }

  stopTyping(): void { this.chatSvc.sendTyping(this.selectedRoom, false); }

  isMine(msg: ChatMessage): boolean {
    const user = this.authSvc.getCurrentUser();
    return msg.senderName === user?.name || msg.senderName === user?.email;
  }

  showDateSep(msg: ChatMessage, prev?: ChatMessage): boolean {
    if (!prev) return true;
    return new Date(msg.sentAt).toDateString() !== new Date(prev.sentAt).toDateString();
  }

  statusLabel(status: string): string {
    return { WAITING: 'Aguardando', REPLIED: 'Respondido', NEW: 'Novo' }[status] ?? status;
  }

  roleLabel(role: string): string {
    return { ADMIN: 'Admin', MANAGER: 'Gerente', SUPPORT: 'Suporte', CUSTOMER: 'Cliente' }[role] ?? role;
  }

  private scrollToBottom(): void {
    try { this.msgContainer.nativeElement.scrollTop = this.msgContainer.nativeElement.scrollHeight; } catch {}
  }
}
