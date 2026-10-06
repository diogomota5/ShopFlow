export interface ApiResponse<T> {
  message: string;
  data: T;
  status: number;
  timestamp?: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}
