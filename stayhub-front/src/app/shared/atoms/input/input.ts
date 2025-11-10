import { Component, Input, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'app-input',
  standalone: true,
  templateUrl: './input.html',
  styleUrls: ['./input.css'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => InputComponent),
    multi: true
  }]
})
export class InputComponent implements ControlValueAccessor {
  @Input() type: string = 'text';
  @Input() placeholder: string = '';
  @Input() disabled: boolean = false;
  @Input() hasError: boolean = false;

  value: string = '';

  onChange: any = () => {};
  onTouched: any = () => {};

  onInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.value = input.value;
    this.onChange(this.value);
  }

  writeValue(value: any): void {
    this.value = value || '';
  }

  // Angular registra la función para notificar cambios
  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  // Angular registra la función para notificar cuando se toca
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  // Angular llama esto para habilitar/deshabilitar
  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }
}
