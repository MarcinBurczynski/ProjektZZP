import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  imports: [RouterOutlet],
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  ngOnInit() {
    const token = localStorage.getItem('authToken');
    if (!token) {
      console.log('Brak tokenu, przekierowanie na stronę logowania');
    }
  }
}
