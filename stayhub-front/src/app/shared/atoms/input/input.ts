import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-input',
  standalone: true,
  imports: [CommonModule],
  template: `<input
    [id]="id"
    [type]="type"
    [placeholder]="placeholder"
    [value]="value"
    (input)="onInput($event)"
    [class.error]="error"
  />`,
  styleUrls: ['./input.css']
})
export class InputComponent {
  @Input() type = 'text';
  @Input() placeholder = '';
  @Input() value = '';
  @Input() error = false;
  @Output() valueChange = new EventEmitter<string>();

  id = `input-${Math.random().toString(36).substring(2, 9)}`;

  onInput(event: Event) {
    this.valueChange.emit((event.target as HTMLInputElement).value);
  }
}
