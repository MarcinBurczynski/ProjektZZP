import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCategoryPopupComponent } from './edit-category-popup.component';

describe('EditCategoryPopupComponent', () => {
  let component: EditCategoryPopupComponent;
  let fixture: ComponentFixture<EditCategoryPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditCategoryPopupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditCategoryPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
