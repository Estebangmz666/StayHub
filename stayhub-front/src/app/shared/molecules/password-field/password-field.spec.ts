import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PasswordField } from './password-field';

describe('PasswordField', () => {
  let component: PasswordField;
  let fixture: ComponentFixture<PasswordField>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PasswordField]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PasswordField);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
