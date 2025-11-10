import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-error-text',
  standalone: true,
  templateUrl: './error-text.html',
  imports: [],
  styleUrls: ['./error-text.css']
})
export class ErrorText{
  @Input() message: string = '';
}
