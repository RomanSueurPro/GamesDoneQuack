import { Component, Inject } from '@angular/core';
import { MatDialogClose, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
@Component({
  selector: 'app-delete-dialog',
  standalone: true,
  imports: [[MatDialogClose, MatButtonModule],],
  templateUrl: './delete-dialog.component.html',
  styleUrl: './delete-dialog.component.scss'
})
export class DeleteDialogComponent {

  constructor(@Inject(MAT_DIALOG_DATA) public data: any){
    
  }
}
