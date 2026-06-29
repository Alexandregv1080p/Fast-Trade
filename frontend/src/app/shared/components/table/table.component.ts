import { Component, Input, Output, EventEmitter, ContentChildren, QueryList, Directive, TemplateRef } from '@angular/core';
import { NgFor, NgIf, NgTemplateOutlet } from '@angular/common';

@Directive({ selector: '[appTableCol]', standalone: true })
export class TableColDirective {
  @Input() appTableCol = '';
  @Input() label = '';
  constructor(public tpl: TemplateRef<any>) {}
}

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [NgFor, NgIf, NgTemplateOutlet, TableColDirective],
  template: `
    <div class="table-wrap">
      <table class="table">
        <thead>
          <tr>
            <th *ngFor="let col of cols">{{ col.label }}</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let row of data; let i = index">
            <td *ngFor="let col of cols">
              <ng-template [ngTemplateOutlet]="col.tpl" [ngTemplateOutletContext]="{ $implicit: row, index: i }" />
            </td>
          </tr>
          <tr *ngIf="!data?.length">
            <td [colSpan]="cols.length" class="table__empty">Nenhum registro encontrado.</td>
          </tr>
        </tbody>
      </table>
    </div>`,
  styles: [`
    .table-wrap { overflow-x: auto; border-radius: var(--radius-md); }
    .table { width: 100%; border-collapse: collapse; background: var(--color-surface); }
    th {
      padding: 12px 16px; text-align: left; font-size: 12px; font-weight: 600;
      color: var(--color-text-muted); text-transform: uppercase; letter-spacing: .5px;
      border-bottom: 2px solid var(--color-border); white-space: nowrap; background: var(--color-surface);
    }
    td { padding: 14px 16px; font-size: 13px; border-bottom: 1px solid var(--color-border); vertical-align: middle; }
    tr:last-child td { border-bottom: none; }
    tr:hover td { background: var(--color-bg); }
    .table__empty { text-align: center; color: var(--color-text-muted); padding: 40px; }
  `]
})
export class TableComponent {
  @Input() data: any[] = [];
  @ContentChildren(TableColDirective) cols!: QueryList<TableColDirective>;
}
