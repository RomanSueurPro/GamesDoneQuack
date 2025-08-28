import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectionPopUpComponent } from './connection-pop-up.component';

describe('ConnectionPopUpComponent', () => {
  let component: ConnectionPopUpComponent;
  let fixture: ComponentFixture<ConnectionPopUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConnectionPopUpComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ConnectionPopUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
