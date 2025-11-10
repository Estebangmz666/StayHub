import { Component, Input, forwardRef } from '@angular/core';
import {ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR} from '@angular/forms';
import {Label} from '../../atoms/label/label';
import {ErrorText} from '../../atoms/error-text/error-text';

@Component({
  selector: 'app-password-field',
  standalone: true,
  templateUrl: './password-field.html',
  styleUrls: ['./password-field.css'],
  imports: [
    Label,
    FormsModule,
    ErrorText
  ],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => PasswordFieldComponent),
    multi: true
  }]
})
export class PasswordFieldComponent implements ControlValueAccessor {
  @Input() label: string = '';
  @Input() placeholder: string = '';
  @Input() required: boolean = false;
  @Input() errorMessage: string = '';

  value: string = '';
  showPassword: boolean = false;

  onChange: any = () => {};
  onTouched: any = () => {};

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  onValueChange(newValue: string): void {
    this.value = newValue;
    this.onChange(newValue);
  }

  writeValue(value: any): void {
    this.value = value || '';
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }
}
