import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { VerifyEmailComponent } from './features/auth/verify-email/verify-email.component';
import { ForgotPasswordComponent } from './features/auth/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './features/auth/reset-password/reset-password.component';
import { ProductsComponent } from './features/products/products/products.component';
import { ProductDetailComponent } from './features/products/product-detail/product-detail.component';
import { CartComponent } from './features/cart/cart/cart.component';
import { CheckoutComponent } from './features/checkout/checkout/checkout.component';
import { OrdersComponent } from './features/orders/orders/orders.component';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';
import { AdminDashboardComponent } from './features/admin/admin-dashboard/admin-dashboard.component';
import { CreateProductComponent } from './features/admin/create-product/create-product.component';
import { CreateCategoryComponent } from './features/admin/create-category/create-category.component';
import { CreateCouponComponent } from './features/admin/create-coupon/create-coupon.component';
import { ManageUsersComponent } from './features/admin/manage-users/manage-users.component';
import { UserProfileEditComponent } from './features/user/user-profile-edit/user-profile-edit.component';
import { ManageOrdersComponent } from './features/admin/manage-orders/manage-orders.component';
import { adminGuard } from './core/guards/admin.guard';
import { LegalPageComponent } from './features/legal/legal-page.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },
  { path: 'verify-email', component: VerifyEmailComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'privacidade', component: LegalPageComponent, data: { type: 'privacy' } },
  { path: 'termos', component: LegalPageComponent, data: { type: 'terms' } },
  { path: 'products', component: ProductsComponent },
  { path: 'products/:id', component: ProductDetailComponent },
  { path: 'cart', component: CartComponent, canActivate: [authGuard] },
  { path: 'checkout', component: CheckoutComponent, canActivate: [authGuard] },
  { path: 'orders', component: OrdersComponent, canActivate: [authGuard] },
  { path: 'profile/edit', component: UserProfileEditComponent, canActivate: [authGuard] },
  {
    path: 'admin',
    component: AdminDashboardComponent,
    canActivate: [authGuard, adminGuard],
    children: [
      { path: 'product/new', component: CreateProductComponent },
      { path: 'category/new', component: CreateCategoryComponent },
      { path: 'coupon/new', component: CreateCouponComponent },
      { path: 'orders', component: ManageOrdersComponent },
      { path: 'users', component: ManageUsersComponent },
      { path: 'users/edit/:id', component: UserProfileEditComponent },
    ],
  },
  { path: '**', redirectTo: '' },
];
