import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InputComponent } from '../atoms/input';
import { LabelComponent } from '../atoms/label';
import { ErrorMessageComponent } from '../atoms/error-message';

@Component({
  selector: 'app-form-field',
  standalone: true,
  imports: [CommonModule, InputComponent, LabelComponent, ErrorMessageComponent],
  template: `
    <div class="form-field">
      @if (label) {
        <app-label [for]="id" [text]="label"></app-label>
      }
      <app-input
        [id]="id"
        [type]="type"
        [placeholder]="placeholder"
        [value]="value"
        [error]="!!error"
        (valueChange)="onValueChange($event)"
      ></app-input>
      <app-error-message [message]="error"></app-error-message>
    </div>
  `,
  styleUrls: ['./form-field.css']
})
export class FormFieldComponent {
  @Input() label = '';
  @Input() type = 'text';
  @Input() placeholder = '';
  @Input() error = '';
  @Input() value = '';

  id = `field-${Math.random().toString(36).substring(2, 9)}`;

  onValueChange(val: string) {
    this.value = val;
  }
}
