import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-label',
  standalone: true,
  imports: [CommonModule],
  template: `<label [for]="for">{{ text }}</label>`,
  styleUrls: ['./label.css']
})
export class LabelComponent {
  @Input() for = '';
  @Input() text = '';
}
