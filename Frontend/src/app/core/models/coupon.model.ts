export interface CouponResponse {
  id: string;
  code: string;
  discountType: 'PERCENTAGE' | 'FIXED';
  discountValue: number;
  minPurchaseValue: number | null;
  expirationDate: string | null;
  maxUses: number | null;
  userCount: number;
  active: boolean;
}
