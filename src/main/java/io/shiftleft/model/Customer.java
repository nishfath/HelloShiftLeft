package io.shiftleft.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Customer {
  public Customer() {
  }

public Customer(String customerId, int clientId, String firstName, String lastName, Date dateOfBirth, String ssn,
      String socialInsurancenum, String tin, String phoneNumber, Address address, Set<Account> accounts) {
    super();
    // Store sanitized values to prevent XSS at data layer
    this.clientId = clientId;
    this.customerId = sanitizeField(customerId);
    this.firstName = sanitizeField(firstName);
    this.lastName = sanitizeField(lastName);
    this.dateOfBirth = dateOfBirth;
    this.ssn = sanitizeField(ssn);
    this.socialInsurancenum = sanitizeField(socialInsurancenum);
    this.tin = sanitizeField(tin);
    this.phoneNumber = sanitizeField(phoneNumber);
    this.address = address;
    this.accounts = accounts;
}

// Helper method to sanitize individual fields
private String sanitizeField(String value) {
    if (value == null) {
        return null;
    }
    // Remove potential XSS vectors while preserving data
    return value.replaceAll("[<>\"'&]", "");
}


  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  private String customerId;

  private int clientId;

  private String firstName;

  private String lastName;

  private Date dateOfBirth;

  private String ssn;

  private String socialInsurancenum;

  private String tin;

  private String phoneNumber;

  @OneToOne(cascade = { CascadeType.ALL })
  private Address address;

  @OneToMany(cascade = { CascadeType.ALL })
  private Set<Account> accounts;

  public long getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public int getClientId() {
    return clientId;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public Date getDateOfBirth() {
    return dateOfBirth;
  }

  public String getSsn() {
    return ssn;
  }

  public String getSocialInsurancenum() {
    return socialInsurancenum;
  }

  public String getTin() {
    return tin;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public Address getAddress() {
    return address;
  }

  public Set<Account> getAccounts() {
    return accounts;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public void setClientId(int clientId) {
    this.clientId = clientId;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public void setSsn(String ssn) {
    this.ssn = ssn;
  }

  public void setSocialInsurancenum(String socialInsurancenum) {
    this.socialInsurancenum = socialInsurancenum;
  }

  public void setTin(String tin) {
    this.tin = tin;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public void setAccounts(Set<Account> accounts) {
    this.accounts = accounts;
  }

@Override
public String toString() {
    // This method should only be used for logging, not for HTML output
    return "Customer [id=" + id + ", customerId=" + customerId + ", clientId=" + clientId + ", firstName=" + firstName
        + ", lastName=" + lastName + ", dateOfBirth=" + dateOfBirth + ", ssn=" + ssn + ", socialInsurancenum="
        + socialInsurancenum + ", tin=" + tin + ", phoneNumber=" + phoneNumber + ", address=" + address + ", accounts="
        + accounts + "]";
}

// New method for safe HTML output
public String toSafeString() {
    // Return sanitized string suitable for HTML contexts
    return "Customer [id=" + escapeHtml(String.valueOf(id)) 
        + ", customerId=" + escapeHtml(customerId) 
        + ", clientId=" + clientId 
        + ", firstName=" + escapeHtml(firstName)
        + ", lastName=" + escapeHtml(lastName) 
        + ", dateOfBirth=" + escapeHtml(String.valueOf(dateOfBirth)) 
        + ", ssn=" + maskSensitiveData(ssn)
        + ", socialInsurancenum=" + maskSensitiveData(socialInsurancenum)
        + ", tin=" + maskSensitiveData(tin)
        + ", phoneNumber=" + escapeHtml(phoneNumber) 
        + ", address=" + escapeHtml(String.valueOf(address))
        + ", accounts=" + escapeHtml(String.valueOf(accounts)) + "]";
}

// Helper method to escape HTML entities
private String escapeHtml(String input) {
    if (input == null) {
        return "null";
    }
    return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
}

// Helper method to mask sensitive data
private String maskSensitiveData(String data) {
    if (data == null || data.length() <= 4) {
        return "****";
    }
    return "****" + data.substring(data.length() - 4);
}


}
