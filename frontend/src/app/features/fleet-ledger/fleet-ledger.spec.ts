import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FleetLedger } from './fleet-ledger';

describe('FleetLedger', () => {
  let component: FleetLedger;
  let fixture: ComponentFixture<FleetLedger>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FleetLedger],
    }).compileComponents();

    fixture = TestBed.createComponent(FleetLedger);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
