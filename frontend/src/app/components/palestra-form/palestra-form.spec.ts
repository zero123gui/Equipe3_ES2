import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PalestraForm } from './palestra-form';

describe('PalestraForm', () => {
  let component: PalestraForm;
  let fixture: ComponentFixture<PalestraForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PalestraForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PalestraForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
