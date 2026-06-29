import { Injectable, inject, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Subject } from 'rxjs';
import { Client, IMessage } from '@stomp/stompjs';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

export interface RoomSummary {
  room: string;
  userName: string;
  userEmail: string;
  lastMessage: string;
  lastMessageAt: string;
  unreadCount: number;
  status: 'NEW' | 'WAITING' | 'REPLIED';
}

export interface ChatMessage {
  id?: number;
  room: string;
  senderId: number;
  senderName: string;
  senderRole: string;
  content: string;
  type: 'TEXT' | 'SYSTEM' | 'TYPING';
  sentAt: string;
  readByAdmin: boolean;
}

export interface TypingEvent {
  user: string;
  typing: string;
}

@Injectable({ providedIn: 'root' })
export class ChatService implements OnDestroy {
  private http   = inject(HttpClient);
  private auth   = inject(AuthService);
  private client!: Client;

  private connected$ = new BehaviorSubject<boolean>(false);
  isConnected$ = this.connected$.asObservable();

  private internalMessages$ = new BehaviorSubject<ChatMessage[]>([]);
  internalMessages = this.internalMessages$.asObservable();

  private supportMessages$ = new BehaviorSubject<Record<string, ChatMessage[]>>({});
  supportMessages = this.supportMessages$.asObservable();

  private newMessage$ = new Subject<ChatMessage>();
  newMessage = this.newMessage$.asObservable();

  private typing$ = new Subject<{ room: string } & TypingEvent>();
  typing = this.typing$.asObservable();

  private unread$ = new BehaviorSubject<{ internal: number; support: number; total: number }>({ internal: 0, support: 0, total: 0 });
  unread = this.unread$.asObservable();

  private roomSummaries$ = new BehaviorSubject<RoomSummary[]>([]);
  roomSummaries = this.roomSummaries$.asObservable();

  private newSupportRequest$ = new Subject<{ room: string; senderName: string; content: string }>();
  newSupportRequest = this.newSupportRequest$.asObservable();

  private baseUrl = environment.apiUrl;

  connect(): void {
    if (this.client?.active) return;
    const token = this.auth.getToken();

    this.client = new Client({
      brokerURL: `${this.baseUrl.replace('http', 'ws').replace('/api', '')}/ws/websocket`,
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 3000,
      onConnect: () => {
        this.connected$.next(true);
        this.subscribeInternal();
        this.subscribeNewSupport();
        this.loadUnread();
        this.loadRoomsDetails();
      },
      onDisconnect: () => this.connected$.next(false),
      onStompError: () => this.connected$.next(false),
    });

    this.client.activate();
  }

  disconnect(): void {
    this.client?.deactivate();
    this.connected$.next(false);
  }

  private subscribeInternal(): void {
    this.client.subscribe('/topic/internal', (msg: IMessage) => {
      const m: ChatMessage = JSON.parse(msg.body);
      this.internalMessages$.next([...this.internalMessages$.value, m]);
      this.newMessage$.next(m);
    });
    this.client.subscribe('/topic/internal.typing', (msg: IMessage) => {
      this.typing$.next({ room: 'internal', ...JSON.parse(msg.body) });
    });
  }

  subscribeSupport(room: string): void {
    if (!this.client?.active) return;
    this.client.subscribe(`/topic/${room}`, (msg: IMessage) => {
      const m: ChatMessage = JSON.parse(msg.body);
      const current = this.supportMessages$.value;
      this.supportMessages$.next({ ...current, [room]: [...(current[room] ?? []), m] });
      this.newMessage$.next(m);
    });
    this.client.subscribe(`/topic/${room}.typing`, (msg: IMessage) => {
      this.typing$.next({ room, ...JSON.parse(msg.body) });
    });
  }

  sendMessage(room: string, content: string): void {
    if (!this.client?.active || !content.trim()) return;
    this.client.publish({
      destination: '/app/chat.send',
      body: JSON.stringify({ room, content }),
    });
  }

  sendTyping(room: string, typing: boolean): void {
    if (!this.client?.active) return;
    this.client.publish({
      destination: '/app/chat.typing',
      body: JSON.stringify({ room, typing: String(typing) }),
    });
  }

  loadHistory(room: string): void {
    this.http.get<ChatMessage[]>(`${this.baseUrl}/chat/history/${room}`).subscribe({
      next: msgs => {
        if (room === 'internal') {
          this.internalMessages$.next(msgs);
        } else {
          const current = this.supportMessages$.value;
          this.supportMessages$.next({ ...current, [room]: msgs });
        }
      },
      error: () => {},
    });
  }

  loadSupportRooms(): Promise<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/chat/rooms`).toPromise().then(r => r ?? []);
  }

  loadRoomsDetails(): void {
    this.http.get<RoomSummary[]>(`${this.baseUrl}/chat/rooms/details`).subscribe({
      next: r => this.roomSummaries$.next(r ?? []),
      error: () => {}
    });
  }

  private subscribeNewSupport(): void {
    if (!this.client?.active) return;
    this.client.subscribe('/topic/support.new', (msg) => {
      const data = JSON.parse(msg.body);
      this.newSupportRequest$.next(data);
      this.loadRoomsDetails();
      this.loadUnread();
    });
  }

  requestSupport(name: string, email: string): Promise<{ room: string }> {
    return this.http.post<{ room: string }>(`${this.baseUrl}/chat/support/request`, { name, email })
      .toPromise().then(r => r!);
  }

  markRead(room: string): void {
    this.http.post(`${this.baseUrl}/chat/read/${room}`, {}).subscribe(() => this.loadUnread());
  }

  loadUnread(): void {
    this.http.get<{ internal: number; support: number; total: number }>(`${this.baseUrl}/chat/unread`).subscribe({
      next: u => this.unread$.next(u),
      error: () => {},
    });
  }

  ngOnDestroy(): void { this.disconnect(); }
}
