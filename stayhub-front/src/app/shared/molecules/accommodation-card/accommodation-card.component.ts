import { Component, Input, Output, EventEmitter } from '@angular/core';
import { AccommodationResponseDTO } from '../../../models/accommodation';
import { AccommodationUtils } from '../../../utils/accommodation';

@Component({
  selector: 'app-accommodation-card',
  standalone: true,
  templateUrl: './accommodation-card.component.html',
  styleUrls: ['./accommodation-card.component.css']
})
export class AccommodationCardComponent {
  @Input() accommodation!: AccommodationResponseDTO;
  @Output() cardClick = new EventEmitter<number>();

  utils = AccommodationUtils;

  onCardClick(): void {
    this.cardClick.emit(this.accommodation.id);
  }

  getCapacityText(): string {
    const capacity = this.accommodation.capacity;
    return capacity === 1 ? '1 huésped' : `${capacity} huéspedes`;
  }
}
