package com.icsd.dto.common;

public class messages {
public static final String FILE_EMPTY="Uploaded file is empty.";
public static final String FILE_TOO_LARGE="File size must not exceed 2MB.";
public static final String FILE_TOO_SMALL="File size must be greater than 20KB.";
public static final String INVALID_FILE_TYPE="Only .jpg image files are allowed.";
public static final String UPLOAD_DIR = "C:\\Users\\Hp\\Desktop\\imgs";




//CUSTOMER TABLE
public static final String VALID_USER= "user is validated";
public static final String CUSTOMER_FOUND="customer found ";
public static final String CUSTOMER_CREATE = "Customer Created Successfully";
public static final String CUSTOMER_NOT_FOUND ="no customer found";




//ADDRESS TABLE
public static final String ADDRESS_NULL= "address with null value";
public static final String ADDRESS_NOT_NULL= "address with not null value";



//ACCOUNT TABLE
public static final String ACCOUNT_CREATED= "account created successfully";
public static final String ACCOUNT_NOT_FOUND= "No Accounts found";
public static final String ACCOUNTS= "list of accounts";
public static final String DELETE_ACCOUNT= "Account Deleted successfully";
public static final String AMOUNT= "Fetched Balance";



//DOCUMENT TABLE 
public static final String DOCUMENT_UPLOAD="Document uploaded Successfully";
public static final String NO_DOCUMENT= "NO record found";
public static final String DOCUMENT_FOUND= "document found";
public static final String DOCUMENT_FOUND_DELETE= "record found and deleted successfully!!";



//TRANSACTION TABLE
public static final String TRANSACTION_COMPLETED="Transaction completed Successfully";
public static final String NEGATIVE_AMOUNT="Amount out of range";
public static final String INSUFFICIENT_BALANCE="not enough balance to complete the transaction";


//EXCEPTION
public static final String RESOURCE_NOT_FOUND="not record found";



//VALIDATION

public static final String DATA_VALIDATION="validation failed";

public static final String OPENING_BAL_MIN="opening balance must be between 1-1000";
public static final String OPENING_BAL_MAX = "opening balance must be between 1-1000";

public static final String AMOUNT_MIN="Amount must be between 1-10000";
public static final String AMOUNT_MAX="Amount must be between 1-10000";
public static final String ACCOUNT_NUMBER_MIN="account number must be greater than  1";

public static final String NOT_BLANK="field can not be blank";
public static final String NOT_NULL="field can not be null";
public static final String NAME_SIZE="name should be between 2 to 60 characters";

public static final String EMAIL="please enter valid email id";
public static final String PASSWORD_PATTERN="password shoulde be 8 characters long and must contain an uppercase alphabet,a lowercase alphabet, a digit, and a special character";
public static final String CONTACT_PATTERN="Contact number must be exactly 10 digits";
public static final String PINCODE_PATTERN="Pincode must be exactly of 6 digits";




//SCHEDULERS
public static final String SUBSCRIPTION_SUBJECT="your Subscription is expiring soon";









}
