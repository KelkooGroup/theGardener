import {Injectable} from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';


@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private snackBar: MatSnackBar) {
  }

  showInfo(msg: string): void {
    this.snackBar.open(msg, 'OK', {
      duration: 3000,
      panelClass: [],
      verticalPosition: 'top'
    });
  }

  showSuccess(msg: string): void {
    this.snackBar.open(msg, 'OK', {
      duration: 3000,
      panelClass: ['success'],
      verticalPosition: 'top'
    });
  }

  showError(msg: string, error?: any): void {
    if (error) {
      console.log(msg, error);
    }
    this.snackBar.open(msg, 'OK', {
      duration: 5000,
      panelClass: ['warning'],
      verticalPosition: 'top'
    });
  }

}
