import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorText } from './error-text';

describe('ErrorText', () => {
  let component: ErrorText;
  let fixture: ComponentFixture<ErrorText>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ErrorText]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ErrorText);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
