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

default: classes

classes: $(SOURCE:.java=.class)

clean:
	$(RM) *~ *.class