import { Component } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";

@Component({
    selector: 'gs-join-select-dialog',
    templateUrl: './join-select-dialog.component.html',
    styleUrls: ['./join-select-dialog.component.scss'],
  })
export class JoinSelectDialogComponent {
    
    constructor(
        private dialogRef: MatDialogRef<JoinSelectDialogComponent>
    ) {
        dialogRef.disableClose = true;
    }

    onSelect(asPlayer: boolean) {
        this.dialogRef.close(asPlayer);
    }
}