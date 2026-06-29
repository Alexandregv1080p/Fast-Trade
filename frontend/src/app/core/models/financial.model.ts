export interface Transaction {
  id: string; userId: string; userName?: string; type: 'CREDIT' | 'DEBIT' | 'TRANSFER';
  amount: number; description?: string; paymentMethod?: string;
  status: 'PENDING' | 'COMPLETED' | 'FAILED'; createdAt: string;
}

export interface Wallet { id: string; userId: string; balance: number; currency: string; updatedAt: string; }
export interface Commission { id: string; orderId: string; amount: number; rate: number; status: string; createdAt: string; }
