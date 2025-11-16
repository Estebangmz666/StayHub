import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyAccommodationsComponent } from './my-accommodations.component';

describe('MyAccommodationsComponent', () => {
  let component: MyAccommodationsComponent;
  let fixture: ComponentFixture<MyAccommodationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyAccommodationsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MyAccommodationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
