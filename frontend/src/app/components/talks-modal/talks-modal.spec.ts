import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TalksModal } from './talks-modal';

describe('TalksModal', () => {
  let component: TalksModal;
  let fixture: ComponentFixture<TalksModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TalksModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TalksModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
