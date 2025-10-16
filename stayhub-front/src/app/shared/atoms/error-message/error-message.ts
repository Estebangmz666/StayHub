import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-error-message',
  standalone: true,
  imports: [CommonModule],
  template: `<div class="error" *ngIf="message">{{ message }}</div>`,
  styleUrls: ['./error-message.css']
})
export class ErrorMessageComponent {
  @Input() message = '';
}
