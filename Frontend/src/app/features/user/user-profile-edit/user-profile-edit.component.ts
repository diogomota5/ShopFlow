import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../../../core/services/user.service';
import { UserResponse } from '../../../core/models/user.model';
import { AuthService } from '../../../core/services/auth.service';
import { AddressService } from '../../../core/services/address.service';
import { AddressesResponse } from '../../../core/models/address.model';
import { CommonModule } from '@angular/common';
import { ConfirmDialogService } from '../../../core/services/confirm-dialog.service';

@Component({
  selector: 'app-user-profile-edit',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './user-profile-edit.component.html',
  styleUrl: './user-profile-edit.component.css',
})
export class UserProfileEditComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private addressService = inject(AddressService);
  private route = inject(ActivatedRoute);
  private confirmService = inject(ConfirmDialogService);

  profileForm: FormGroup;
  addressForm: FormGroup;

  successMessage = '';
  errorMessage = '';
  addressSuccessMessage = '';
  addressErrorMessage = '';

  isLoading = true;
  isLoadingAddresses = false;

  currentUser: UserResponse | null = null;
  isAdmin = false;
  isAdminContext = false;

  get isEditingUserFromAdminPanel(): boolean {
    return this.isAdmin && this.isAdminContext && !!this.route.snapshot.paramMap.get('id');
  }

  addresses: AddressesResponse[] = [];
  showAddressForm = false;
  editingAddressId: string | null = null;

  constructor() {
    this.profileForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      telephone: ['', [Validators.maxLength(20)]],
      role: ['USER'],
      active: [true],
    });

    this.addressForm = this.fb.group({
      nickname: ['', [Validators.maxLength(50)]],
      zipCode: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(9)]],
      street: ['', [Validators.required]],
      streetNumber: ['', [Validators.required]],
      complement: ['', [Validators.maxLength(100)]],
      neighborhood: ['', [Validators.required]],
      city: ['', [Validators.required]],
      state: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(2)]],
      primaryAddress: [false],
    });
  }

  ngOnInit() {
    const fullPath = this.route.snapshot.pathFromRoot
      .map((r) => r.url.map((u) => u.path).join('/'))
      .join('/');
    this.isAdminContext = fullPath.includes('admin');
    this.isAdmin = this.authService.isAdmin();

    this.route.paramMap.subscribe((params) => {
      const id = params.get('id');

      if (id && this.isEditingUserFromAdminPanel) {
        this.userService.findById(id).subscribe({
          next: (res) => this.fillForm(res.data),
          error: () => {
            this.isLoading = false;
            this.errorMessage =
              'Não foi possível carregar os dados do usuário. Verifique sua conexão.';
          },
        });
      } else {
        this.userService.getUserInfo().subscribe({
          next: (res) => this.fillForm(res.data),
          error: () => {
            this.isLoading = false;
            this.errorMessage = 'Não foi possível carregar seu perfil. Verifique sua conexão.';
          },
        });
      }
    });
  }

  private fillForm(user: UserResponse) {
    if (!user) {
      this.isLoading = false;
      this.errorMessage = 'Dados do usuário não encontrados.';
      return;
    }
    this.currentUser = user;
    this.isLoading = false;
    this.profileForm.patchValue({
      name: user.name,
      email: user.email,
      telephone: user.telephone,
      role: user.role,
      active: user.active,
    });

    this.loadAddresses();
  }

  onSubmit() {
    if (this.profileForm.invalid || !this.currentUser) return;

    this.successMessage = '';
    this.errorMessage = '';

    const formValue = this.profileForm.getRawValue();
    const request = this.isEditingUserFromAdminPanel
      ? formValue
      : {
          name: formValue.name,
          email: formValue.email,
          telephone: formValue.telephone,
        };

    this.userService.updateUser(this.currentUser.id, request).subscribe({
      next: () => {
        this.successMessage = 'Perfil atualizado com sucesso!';
      },
      error: (err: any) => {
        this.errorMessage =
          'Erro ao atualizar perfil: ' + (err.error?.message || 'Tente novamente.');
      },
    });
  }

  // --- ADDRESSES SECION ---

  loadAddresses() {
    if (!this.currentUser) return;

    this.isLoadingAddresses = true;
    const requestArgs = this.isEditingUserFromAdminPanel
      ? this.addressService.findAllForUser(this.currentUser.id)
      : this.addressService.findAll();

    requestArgs.subscribe({
      next: (res) => {
        this.addresses = res.data || [];
        this.isLoadingAddresses = false;
      },
      error: () => {
        this.isLoadingAddresses = false;
        this.addressErrorMessage = 'Erro ao carregar endereços do usuário.';
      },
    });
  }

  openAddressForm(address?: AddressesResponse) {
    this.showAddressForm = true;
    this.addressSuccessMessage = '';
    this.addressErrorMessage = '';

    if (address) {
      this.editingAddressId = address.id;
      this.addressForm.patchValue({
        nickname: address.nickname,
        zipCode: address.zipCode,
        street: address.street,
        streetNumber: address.streetNumber,
        complement: address.complement,
        neighborhood: address.neighborhood,
        city: address.city,
        state: address.state,
        primaryAddress: address.primaryAddress,
      });
    } else {
      this.editingAddressId = null;
      this.addressForm.reset({ primaryAddress: false });
    }
  }

  closeAddressForm() {
    this.showAddressForm = false;
    this.editingAddressId = null;
    this.addressForm.reset();
  }

  saveAddress() {
    if (this.addressForm.invalid || !this.currentUser) return;

    this.addressSuccessMessage = '';
    this.addressErrorMessage = '';

    const isAdminEditingOther = this.isEditingUserFromAdminPanel;

    if (this.editingAddressId) {
      this.addressService
        .update(this.editingAddressId, this.addressForm.value, isAdminEditingOther)
        .subscribe({
          next: () => {
            this.addressSuccessMessage = 'Endereço atualizado com sucesso!';
            this.closeAddressForm();
            this.loadAddresses();
          },
          error: (err: any) => {
            this.addressErrorMessage =
              'Erro ao atualizar endereço: ' + (err.error?.message || 'Tente novamente.');
          },
        });
    } else {
      const requestArgs = isAdminEditingOther
        ? this.addressService.createForUser(this.currentUser.id, this.addressForm.value)
        : this.addressService.create(this.addressForm.value);

      requestArgs.subscribe({
        next: () => {
          this.addressSuccessMessage = 'Endereço cadastrado com sucesso!';
          this.closeAddressForm();
          this.loadAddresses();
        },
        error: (err: any) => {
          this.addressErrorMessage =
            'Erro ao adicionar endereço: ' + (err.error?.message || 'Tente novamente.');
        },
      });
    }
  }

  async deleteAddress(addressId: string) {
    const confirmed = await this.confirmService.confirm({
      title: 'Excluir Endereço',
      message:
        'Deseja realmente excluir este endereço? Esta ação não poderá ser desfeita e o endereço será removido permanentemente.',
      confirmText: 'Excluir',
      cancelText: 'Cancelar',
      type: 'danger',
    });

    if (!confirmed) return;

    this.addressSuccessMessage = '';
    this.addressErrorMessage = '';

    const isAdminEditingOther = this.isEditingUserFromAdminPanel;

    this.addressService.delete(addressId, isAdminEditingOther).subscribe({
      next: () => {
        this.addressSuccessMessage = 'Endereço excluído com sucesso!';
        this.loadAddresses();
      },
      error: (err: any) => {
        this.addressErrorMessage =
          'Erro ao excluir endereço: ' + (err.error?.message || 'Tente novamente.');
      },
    });
  }
}
