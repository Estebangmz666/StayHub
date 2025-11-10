import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-link',
  standalone: true,
  templateUrl: './link.html',
  styleUrls: ['./link.css']
})
export class LinkComponent {
  @Input() text: string = '';
  @Output() clicked = new EventEmitter<void>();

  onClick(): void {
    this.clicked.emit();
  }
}
