import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CriarPalestra } from './criar-palestra';

describe('CriarPalestra', () => {
  let component: CriarPalestra;
  let fixture: ComponentFixture<CriarPalestra>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CriarPalestra]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CriarPalestra);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
