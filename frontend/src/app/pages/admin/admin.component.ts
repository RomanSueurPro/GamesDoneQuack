import { Component, ViewChild } from '@angular/core';
import { RoleListComponent } from './role-list/role-list.component';
import { MatTabChangeEvent, MatTabsModule } from '@angular/material/tabs';
import { HeaderComponent } from '../../header/header.component';
import { PermissionListComponent } from './permission-list/permission-list.component';
import { MatDialog } from '@angular/material/dialog';
import { Observable, of } from 'rxjs';
import { ConfirmationDialogComponent } from './confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [RoleListComponent, MatTabsModule, HeaderComponent, PermissionListComponent],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent {
  //reload logic so tabs are up to date with database
  selectedTab = 0;
  previousTab = 0;
  tabTransitionAuthorized = true;
  weJustCanceled = false;

  @ViewChild(PermissionListComponent)
  private permissionList!: PermissionListComponent;

  @ViewChild(RoleListComponent)
  private roleList!: RoleListComponent;

  constructor(
    private confirmDialog: MatDialog
  ) { }

  private dialogOptions = { width: '75rem', height: '15rem', hasBackdrop: true, disableClose: true };

  onTabChanged(event: MatTabChangeEvent): void {

    const requestedTab = event.index;

    if (this.weJustCanceled) {
      this.weJustCanceled = false;
      return;
    }
    if (this.permissionList?.hasUnsavedChanges()) {

      this.permissionList.canLeavePage().subscribe(canLeave => {

        if (canLeave) {
          this.selectedTab = requestedTab;
          this.previousTab = requestedTab;
          this.tabTransitionAuthorized = true;
        } else {
          this.selectedTab = this.previousTab;
          this.tabTransitionAuthorized = false;
          this.weJustCanceled = true;
        }
      });

      return;
    }

    if (this.roleList?.hasUnsavedChanges()) {

      this.roleList.canLeavePage().subscribe(canLeave => {

        if (canLeave) {
          this.selectedTab = requestedTab;
          this.previousTab = requestedTab;
          this.tabTransitionAuthorized = true;
        } else {
          this.selectedTab = this.previousTab;
          this.tabTransitionAuthorized = false;
          this.weJustCanceled = true;
        }
      });

      return;
    }
    this.previousTab = requestedTab;
    this.selectedTab = requestedTab;
  }

  hasUnsavedChanges(): boolean {
    return this.roleList.hasUnsavedChanges() || this.permissionList.hasUnsavedChanges();
  }

  canLeavePage(): Observable<boolean> {

    if (!this.hasUnsavedChanges()) {
      return of(true);
    }

    const dialogRef = this.confirmDialog.open(
      ConfirmationDialogComponent,
      {
        width: this.dialogOptions.width,
        height: this.dialogOptions.height,
        hasBackdrop: this.dialogOptions.hasBackdrop,
        disableClose: this.dialogOptions.disableClose,
        panelClass: ['confirmation-dialog', 'dialog'],
      }
    );

    return dialogRef.afterClosed();
  }
}
