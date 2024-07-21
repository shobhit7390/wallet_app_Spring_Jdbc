package shobhit.wallet_springJdbc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class App
{
	private static String bankName="BNK";
	private WalletManager walletManager;
	private User currentUser;
	
	public App() {
		walletManager=new WalletManager();
	}
	
	public void showMenu() {
		Scanner sc=new Scanner(System.in);
		
		boolean exit=true;
		
		while(exit) {
			if(currentUser==null) {
				System.out.println("1. Create an account");
				System.out.println("2. Login");
				System.out.println("3. Exit");
				System.out.println("Choose an option:");
				int choice=sc.nextInt();sc.nextLine();
				
				switch(choice) {
					case 1:
						createAccount();
						break;
					case 2:
						login();
						break;
					case 3:
						exit=false;
						System.out.println("Thank you...Take care");
						break;
					default:
						System.out.println("Invalid option....Try again !\n");
				}
			} else {
				System.out.println("1. Show balance");
				System.out.println("2. Deposit Money");
				System.out.println("3. Withdraw Money");
				System.out.println("4. Fund transfer");
				System.out.println("5. Print transaction history");
				System.out.println("6. Change your Pin");
				System.out.println("7. Logout");
				System.out.println("8. Exit");
				System.out.println("Choose an option:");
				
				int choice=sc.nextInt();sc.nextLine();
				
				switch(choice) {
				case 1:
					showBalance();
					break;
				case 2:
					depositMoney();
					break;
				case 3:
					withdrawMoney();
					break;
				case 4:
					fundTransfer();
					break;
				case 5:
					printTransactionHistory();
					break;
				case 6:
					changePin();
					break;
				case 7:
					logout();
					break;
				case 8:
					exit=false;
					System.out.println("Thank you...Take care");
					break;
				default:
					System.out.println("Invalid option....Try again !\n");
			}
			}
		}
	}
	
	private void createAccount() {
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter user name: ");
		String userName=sc.nextLine();
		System.out.println("Enter password: ");
		String userPassword=sc.nextLine();
		System.out.println("Enter user email: ");
		String userEmail=sc.nextLine();
		System.out.println("Enter user Phone Number: ");
		String userPhone=sc.nextLine();

		String upiId=userPhone+"@"+bankName.toLowerCase();
		String accountNumber=bankName+"1"+generateRandom(8);
		String userPin=generateRandom(4).toString();

		walletManager.addUser(userName, userPassword, userEmail, userPhone, upiId, accountNumber, userPin);
		System.out.println("Account Created Successfully...!!\n");
		System.out.println("Account Details:"+"\nUpi Id:"+upiId+"\nAccount Number:"+accountNumber+"\nUser Pin:"+userPin+"\n");
	}
	
	private void login() {
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter Phone Number: ");
		String userPhone=sc.nextLine();
		System.out.println("Enter password: ");
		String userPassword=sc.nextLine();
		
		currentUser=walletManager.authenticateUser(userPhone, userPassword);
		if(currentUser!=null) {
			System.out.println("Login Successfull\n");
		} else {
			System.out.println("Invalid user name or password\n");
		}
	}
	
	private void logout() {
		currentUser=null;
		System.out.println("Logged out...!!\n");
	}
	
	private void showBalance() {
		if(currentUser==null) {
			System.out.println("Please login first.");
			return;
		}
		System.out.println("Current balance : " + currentUser.getBalance()+"\n");
	}
	
	public void depositMoney() {
		if(currentUser==null) {
			System.out.println("Please login first.");
			return;
		}
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter amount to deposit : ");
		double amount=sc.nextDouble();
		currentUser.setBalance(currentUser.getBalance()+amount);
		
		walletManager.updateUserBalance(currentUser.getUserId(), currentUser.getBalance());
		
		Transaction transaction=new Transaction();
		transaction.setUserId(currentUser.getUserId());
		transaction.setAmount(amount);
		transaction.setTransactionType("Deposit");
		
		walletManager.addTransaction(transaction);
		System.out.println("Money Deposited Successfully..!!\n");
	}
	
	public void withdrawMoney() {
		if(currentUser==null) {
			System.out.println("Please login first.");
			return;
		}
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter amount to withdraw : ");
		double amount=sc.nextDouble();
		sc.nextLine();
		System.out.println("Enter Your PIN : ");
		String pin=sc.nextLine();

		if(!pin.equals(currentUser.getUserPin())){
			System.out.println("Wrong PIN...Try Again\n");
			return;
		}
		
		if(currentUser.getBalance()<amount) {
			System.out.println("Insufficient balance.");
			return;
		}
		currentUser.setBalance(currentUser.getBalance()-amount);
		
		walletManager.updateUserBalance(currentUser.getUserId(), currentUser.getBalance());
		
		Transaction transaction=new Transaction();
		transaction.setUserId(currentUser.getUserId());
		transaction.setAmount(amount);
		transaction.setTransactionType("Withdraw");
		
		walletManager.addTransaction(transaction);
		System.out.println("Money Withdrawl Successfull..!!\n");
	}
	
	private void fundTransfer() {
		if(currentUser==null) {
			System.out.println("Please login first.");
			return;
		}
		Scanner sc=new Scanner(System.in);

		System.out.println("\nChoose transfer method:");
		System.out.println("1. Upi Id");
		System.out.println("2. Account Number\n");
		System.out.println("Choose an option:");
		int choice=sc.nextInt();sc.nextLine();

		switch(choice) {
			case 1:
				System.out.println("Enter recipient's upi Id : ");
				String recipientUpiId=sc.nextLine();
				fundTransferApi(recipientUpiId, "UPI ID");
				break;
			case 2:
				System.out.println("Enter recipient's Account Number : ");
				String recipientAccNo=sc.nextLine();
				fundTransferApi(recipientAccNo, "ACC NO");
				break;
			default:
				System.out.println("Invalid option....Try again !\n");
		}
	}

	public void fundTransferApi(String recipientAccNo, String methodIdentifier){
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter amount to transfer : ");
		double amt=sc.nextDouble();
		sc.nextLine();
		System.out.println("Enter Your PIN : ");
		String pin=sc.nextLine();

		if(!pin.equals(currentUser.getUserPin())){
			System.out.println("Wrong PIN...Try Again\n");
			return;
		}

		if(currentUser.getBalance()<amt) {
			System.out.println("Insufficient balance.\n");
			return;
		}

		User recipientUser2=walletManager.getUserBySpecifier(recipientAccNo);
		if(recipientUser2==null) {
			System.out.println("Recipient User Not Found\n");
			return;
		}

		if(recipientUser2.getUserId()==currentUser.getUserId()) {
			System.out.println("Fund transfer not allowed to self account...Please try any other..!\n");
			return;
		}

		currentUser.setBalance(currentUser.getBalance()-amt);
		walletManager.updateUserBalance(currentUser.getUserId(), currentUser.getBalance());

		recipientUser2.setBalance(recipientUser2.getBalance()+amt);
		walletManager.updateUserBalance(recipientUser2.getUserId(), recipientUser2.getBalance());

		Transaction trans=new Transaction();
		trans.setUserId(currentUser.getUserId());
		trans.setAmount(amt);
		if(methodIdentifier.equals("UPI ID")){
			trans.setTransactionType("Transferred To "+methodIdentifier+": "+recipientUser2.getUpiId());
		} else{
			trans.setTransactionType("Transferred To "+methodIdentifier+": "+recipientUser2.getAccountNumber());
		}

		walletManager.addTransaction(trans);

		Transaction recipientTrans=new Transaction();
		recipientTrans.setUserId(recipientUser2.getUserId());
		recipientTrans.setAmount(amt);
		if(methodIdentifier.equals("UPI ID")){
			recipientTrans.setTransactionType("Received From  "+methodIdentifier+": "+currentUser.getUpiId());
		} else{
			recipientTrans.setTransactionType("Received From  "+methodIdentifier+": "+currentUser.getAccountNumber());
		}
		walletManager.addTransaction(recipientTrans);

		System.out.println("Transaction Successful...!\n");
	}


	public Integer generateRandom(int nDigit){
		Random range=new Random();
		int min=(int) Math.pow(10, nDigit-1);
		int max=(int) (Math.pow(10, nDigit)-1);
		return range.nextInt((max-min)+1)+min;
	}
	
	
	public void printTransactionHistory() {
		if(currentUser==null) {
			System.out.println("Please login first.");
			return;
		}
		List<Transaction> transactions=walletManager.getTransactionHistory(currentUser.getUserId());
		System.out.println("Transaction History : ");
		
		for(Transaction t:transactions) {
			System.out.println(t.getTransTimestamp()+" -- "+t.getTransactionType()+": "+t.getAmount());
		}
		System.out.print("\n");
	}

	public void changePin(){
		Scanner sc=new Scanner(System.in);
		if(currentUser==null) {
			System.out.println("Please login first.");
			return;
		}

		System.out.println("Enter Your Old PIN : ");
		String oldPin=sc.nextLine();
		System.out.println("Enter Your New PIN : ");
		String newPin=sc.nextLine();

		if(!currentUser.getUserPin().equals(oldPin)){
			System.out.println("Old PIN Mismatched\n");
			return;
		}

		if(oldPin.equals(newPin)){
			System.out.println("You Entered the Same new PIN... Please Enter some new PIN\n");
			return;
		} else{
			walletManager.modifyPin(currentUser.getUserId(), newPin);
			currentUser.setUserPin(newPin);
			System.out.println("Your PIN changed !!\n");
		}
	}

	
	
    public static void main( String[] args )
    {
		ApplicationContext context=new ClassPathXmlApplicationContext("context.xml");
    	App app=new App();
    	System.out.println("*** Wallet Manager Powered By "+ bankName + " ***\n");
    	app.showMenu();
    }
}
