#JFLAGS = -g
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

CLASSES = \
	Jog.java \
	Login.java \
	CustomerSys.java \
	NewCustomerSys.java \
	ExistCustomerSys.java \
	PhonePurchase.java \
	Billing.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *~ *.class