import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  imports: [RouterOutlet, FormsModule],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('angular-front');
  protected menuOpen = false;
  protected dropdownOpen = false;
  protected searchTerm = '';
  protected searchMessage = '';

  protected toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  protected toggleDropdown(event: Event): void {
    event.preventDefault();
    this.dropdownOpen = !this.dropdownOpen;
  }

  protected closeMenus(): void {
    this.dropdownOpen = false;
    this.menuOpen = false;
  }

  protected search(): void {
    const term = this.searchTerm.trim();
    this.searchMessage = term ? `Searching for “${term}”…` : 'Enter a search term.';
  }
}
