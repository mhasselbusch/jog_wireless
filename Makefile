#JFLAGS = -g
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

SOURCE = \
	Jog.java \
	Login.java \
	CustomerSys.java \
	NewCustomerSys.java \
	ExistCustomerSys.java \
	PhonePurchase.java \
	Billing.java \
	EmployeeSys.java \
	Restock.java \
	AccountCreation.java

CLASSES = \
	Jog.class \
	Login.class \
	CustomerSys.class \
	NewCustomerSys.class \
	ExistCustomerSys.class \
	PhonePurchase.class \
	Billing.class \
	EmployeeSys.class \
	Restock.class \
	AccountCreation.class

default: classes

classes: $(SOURCE:.java=.class)

jar: classes
	jar cfmv mah318.jar Manifest.txt $(CLASSES)

clean:
	$(RM) *~ *.class mah318.jar