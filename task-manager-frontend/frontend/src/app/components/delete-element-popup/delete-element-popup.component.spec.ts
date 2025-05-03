import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteElementPopupComponent } from './delete-element-popup.component';

describe('DeleteElementPopupComponent', () => {
  let component: DeleteElementPopupComponent;
  let fixture: ComponentFixture<DeleteElementPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteElementPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeleteElementPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
