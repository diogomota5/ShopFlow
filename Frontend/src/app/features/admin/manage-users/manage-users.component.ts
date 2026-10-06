import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { UserResponse } from '../../../core/models/user.model';

@Component({
  selector: 'app-manage-users',
  standalone: true,
  imports: [RouterModule, DatePipe],
  templateUrl: './manage-users.component.html',
  styleUrl: './manage-users.component.css',
})
export class ManageUsersComponent implements OnInit {
  private userService = inject(UserService);

  users = signal<UserResponse[]>([]);
  loading = signal(true);
  error = signal('');

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);
    this.userService.findAll().subscribe({
      next: (res) => {
        if (res.data) {
          this.users.set(res.data);
        }
        this.loading.set(false);
      },
      error: (err: any) => {
        this.error.set('Não foi possível carregar a lista de usuários.');
        this.loading.set(false);
      },
    });
  }
}
