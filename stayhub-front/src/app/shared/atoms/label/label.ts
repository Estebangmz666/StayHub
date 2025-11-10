import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-label',
  standalone: true,
  templateUrl: './label.html',
  imports: [],
  styleUrls: ['./label.css']
})
export class Label{
  @Input() text: string = '';
  @Input() required: boolean = false;
}
