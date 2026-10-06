export interface AssessmentRequest {
  productId: string;
  rating: number;
  title: string;
  comment?: string;
}

export interface AssessmentResponse {
  id: string;
  productId: string;
  userName: string;
  rating: number;
  title: string;
  comment: string | null;
  verified: boolean;
  createdAt: string;
}

export interface ProductRatingResponse {
  averageRating: number;
  totalAssessments: number;
}
