import { Component, Input, forwardRef } from '@angular/core';
import {ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR} from '@angular/forms';
import {Label} from '../../atoms/label/label';
import {InputComponent} from '../../atoms/input/input';
import {ErrorText} from '../../atoms/error-text/error-text';

@Component({
  selector: 'app-form-field',
  standalone: true,
  templateUrl: './form-field.html',
  styleUrls: ['./form-field.css'],
  imports: [
    Label,
    InputComponent,
    FormsModule,
    ErrorText
  ],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => FormField),
    multi: true
  }]
})
export class FormField implements ControlValueAccessor {
  @Input() label: string = '';
  @Input() type: string = 'text';
  @Input() placeholder: string = '';
  @Input() required: boolean = false;
  @Input() errorMessage: string = '';

  value: string = '';
  onChange: any = () => {};
  onTouched: any = () => {};

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
