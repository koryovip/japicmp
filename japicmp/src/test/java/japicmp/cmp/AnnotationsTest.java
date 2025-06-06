package japicmp.cmp;

import japicmp.model.JApiClass;
import japicmp.model.JApiField;
import japicmp.model.JApiMethod;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtFieldBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

import static japicmp.util.Helper.*;
import static org.hamcrest.core.Is.is;

class AnnotationsTest {

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
	public @interface Include {

	}

	@Test
	void testNoAnnotationsClass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setNoAnnotations(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").withAnnotation(Include.class.getName()).addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").withAnnotation(Include.class.getName()).addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(2));
		JApiClass jApiClass = getJApiClass(jApiClasses, "big.bang.theory.Sheldon");
		MatcherAssert.assertThat(jApiClass.getAnnotations().size(), is(0));
	}

	@Test
	void testNoAnnotationsMethod() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setNoAnnotations(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("excel").withAnnotation(Include.class.getName()).addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("excel").withAnnotation(Include.class.getName()).addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(2));
		JApiClass jApiClass = getJApiClass(jApiClasses, "big.bang.theory.Sheldon");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "excel");
		MatcherAssert.assertThat(jApiMethod.getAnnotations().size(), is(0));
	}

	@Test
	void testNoAnnotationsField() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setNoAnnotations(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtFieldBuilder.create().name("age").type(classPool.getCtClass(String.class.getName())).withAnnotation(Include.class.getName()).addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtFieldBuilder.create().name("age").type(classPool.getCtClass(String.class.getName())).withAnnotation(Include.class.getName()).addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(2));
		JApiClass jApiClass = getJApiClass(jApiClasses, "big.bang.theory.Sheldon");
		JApiField jApiField = getJApiField(jApiClass.getFields(), "age");
		MatcherAssert.assertThat(jApiField.getAnnotations().size(), is(0));
	}
}
