import com.guicedee.guicedinjection.interfaces.IGuiceModule;
import com.test.EntityAssistTestDBModule;

module entity.assist.test {

    requires com.entityassist;

    requires jakarta.xml.bind;
    requires org.junit.jupiter.api;

    provides IGuiceModule with EntityAssistTestDBModule;

    opens com.test to org.junit.platform.commons,org.hibernate.orm.core,com.google.guice,net.bytebuddy,com.entityassist;

}