import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteCategoryPopupComponent } from './delete-category-popup.component';

describe('DeleteCategoryPopupComponent', () => {
  let component: DeleteCategoryPopupComponent;
  let fixture: ComponentFixture<DeleteCategoryPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteCategoryPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeleteCategoryPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
