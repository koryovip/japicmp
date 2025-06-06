package japicmp.cmp;

import japicmp.filter.*;
import japicmp.model.JApiChangeStatus;
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
import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;

class FilterTest {

	@Test
	void testOneClassNoExclude() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
	}

	@Test
	void testOneClassExcluded() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavaDocLikeClassFilter("japicmp.Test"));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(0));
	}

	@Test
	void testTwoClassesOneExclude() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavaDocLikeClassFilter("japicmp.Homer"));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
	}

	@Test
	void testTwoClassesTwoExcludeWithWildcard() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavaDocLikeClassFilter("japicmp.*"));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(0));
	}

	@Test
	void testTwoClassesTwoExcludeWithWildcardOneLetter() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavaDocLikeClassFilter("japicmp.T*"));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Test1").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Test2").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Test1").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Test2").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(0));
	}

	@Test
	void testTwoClassesIncludePackageButExcludeClass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavaDocLikeClassFilter("japicmp.Test1"));
		options.getFilters().getIncludes().add(new JavadocLikePackageFilter("japicmp", false));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Test1").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Test2").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Test1").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Test2").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
	}

	@Test
	void testTwoClassesExcludePackageAndClass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavaDocLikeClassFilter("japicmp.Test1"));
		options.getFilters().getExcludes().add(new JavadocLikePackageFilter("japicmp", false));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Test1").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Test2").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Test1").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Test2").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(0));
	}

	@Test
	void testTwoClassesExcludeClassThatDoesNotExist() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavaDocLikeClassFilter("japicmp.Test1"));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(2));
	}

	@Test
	void testFourClassesFromTwoPackagesExcludeOnePerPackage() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavaDocLikeClassFilter("japicmp.Marge"));
		options.getFilters().getExcludes().add(new JavaDocLikeClassFilter("big.bang.theory.Sheldon"));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
				CtClass ctClass3 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtClass ctClass4 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2, ctClass3, ctClass4);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
				CtClass ctClass3 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtClass ctClass4 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2, ctClass3, ctClass4);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(2));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Leonard"), is(notNullValue()));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "japicmp.Homer"), is(notNullValue()));
	}

	@Test
	void testMethodExcluded() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavadocLikeBehaviorFilter("big.bang.theory.Sheldon#study()"));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(2));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasNoJApiMethodWithName("study"));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasJApiMethodWithName("knowItAll"));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Leonard"), hasJApiMethodWithName("askSheldon"));
	}

	@Test
	void testMethodIncluded() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new JavadocLikeBehaviorFilter("big.bang.theory.Sheldon#study()"));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasJApiMethodWithName("study"));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasNoJApiMethodWithName("knowItAll"));
	}

	@Test
	void testPackageExcludedMethodIncluded() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new JavadocLikePackageFilter("simpsons", false));
		options.getFilters().getIncludes().add(new JavadocLikeBehaviorFilter("big.bang.theory.Sheldon#study()"));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				CtClass ctClass3 = CtClassBuilder.create().name("simpsons.Homer").addToClassPool(classPool);
				CtClass ctClass4 = CtClassBuilder.create().name("simpsons.Marge").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2, ctClass3, ctClass4);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				CtClass ctClass3 = CtClassBuilder.create().name("simpsons.Homer").addToClassPool(classPool);
				CtClass ctClass4 = CtClassBuilder.create().name("simpsons.Marge").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2, ctClass3, ctClass4);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasJApiMethodWithName("study"));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasNoJApiMethodWithName("knowItAll"));
	}

	@Test
	void testPackageIncludedMethodExcluded() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new JavadocLikePackageFilter("big.bang.theory", false));
		options.getFilters().getExcludes().add(new JavadocLikeBehaviorFilter("big.bang.theory.Sheldon#study()"));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				CtClass ctClass3 = CtClassBuilder.create().name("simpsons.Homer").addToClassPool(classPool);
				CtClass ctClass4 = CtClassBuilder.create().name("simpsons.Marge").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2, ctClass3, ctClass4);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				CtClass ctClass3 = CtClassBuilder.create().name("simpsons.Homer").addToClassPool(classPool);
				CtClass ctClass4 = CtClassBuilder.create().name("simpsons.Marge").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2, ctClass3, ctClass4);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(2));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasNoJApiMethodWithName("study"));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasJApiMethodWithName("knowItAll"));
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
	public @interface Exclude {

	}

	@Test
	void testAnnotationClassExcluded() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new AnnotationClassFilter("@" + Exclude.class.getName()));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").withAnnotation(Exclude.class.getName()).addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").withAnnotation(Exclude.class.getName()).addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
		MatcherAssert.assertThat(jApiClasses.get(0).getFullyQualifiedName(), is("big.bang.theory.Leonard"));
	}

	@Test
	void testAnnotationMethodExcluded() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new AnnotationBehaviorFilter("@" + Exclude.class.getName()));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").withAnnotation(Exclude.class.getName()).addToClass(ctClass1);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").withAnnotation(Exclude.class.getName()).addToClass(ctClass1);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(2));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasNoJApiMethodWithName("study"));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasJApiMethodWithName("knowItAll"));
	}

	@Test
	void testAnnotationFieldExcluded() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getExcludes().add(new AnnotationFieldFilter("@" + Exclude.class.getName()));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtFieldBuilder.create().name("age").type(classPool.getCtClass(String.class.getName())).withAnnotation(Exclude.class.getName()).addToClass(ctClass1);
				CtFieldBuilder.create().name("name").type(classPool.getCtClass(String.class.getName())).addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtFieldBuilder.create().name("age").type(classPool.getCtClass(String.class.getName())).withAnnotation(Exclude.class.getName()).addToClass(ctClass1);
				CtFieldBuilder.create().name("name").type(classPool.getCtClass(String.class.getName())).addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(2));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasNoJApiFieldWithName("age"));
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
	public @interface Include {

	}

	@Test
	void testAnnotationClassIncluded() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new AnnotationClassFilter("@" + Include.class.getName()));
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
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
		MatcherAssert.assertThat(jApiClasses.get(0).getFullyQualifiedName(), is("big.bang.theory.Sheldon"));
	}

	@Test
	void testAnnotationMethodIncluded() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new AnnotationBehaviorFilter("@" + Include.class.getName()));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").withAnnotation(Include.class.getName()).addToClass(ctClass1);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").withAnnotation(Include.class.getName()).addToClass(ctClass1);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
		MatcherAssert.assertThat(jApiClasses.get(0).getMethods().size(), is(1));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasJApiMethodWithName("study"));
	}

	@Test
	void testAnnotationFieldIncluded() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new AnnotationFieldFilter("@" + Include.class.getName()));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtFieldBuilder.create().name("age").type(classPool.getCtClass(String.class.getName())).withAnnotation(Include.class.getName()).addToClass(ctClass1);
				CtFieldBuilder.create().name("name").type(classPool.getCtClass(String.class.getName())).addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtFieldBuilder.create().name("age").type(classPool.getCtClass(String.class.getName())).withAnnotation(Include.class.getName()).addToClass(ctClass1);
				CtFieldBuilder.create().name("name").type(classPool.getCtClass(String.class.getName())).addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
		MatcherAssert.assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasJApiFieldWithName("age"));
	}

	@Test
	void testAnnotationClassIncludedChangesDetected() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new AnnotationClassFilter("@" + Include.class.getName()));
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
				CtMethodBuilder.create().publicAccess().returnType(classPool.getCtClass(String.class.getName())).name("getName").addToClass(ctClass1);
				CtFieldBuilder.create().type(classPool.getCtClass(Integer.class.getName())).name("age").addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
		JApiClass jApiClass = getJApiClass(jApiClasses, "big.bang.theory.Sheldon");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "getName");
		MatcherAssert.assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.NEW));
		JApiField jApiField = getJApiField(jApiClass.getFields(), "age");
		MatcherAssert.assertThat(jApiField.getChangeStatus(), is(JApiChangeStatus.NEW));
	}

	@Test
	void testClassIncludedButMethodExcluded() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getFilters().getIncludes().add(new AnnotationClassFilter("@" + Include.class.getName()));
		options.getFilters().getExcludes().add(new AnnotationBehaviorFilter("@" + Exclude.class.getName()));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").withAnnotation(Include.class.getName()).addToClassPool(classPool);
				CtFieldBuilder.create().name("age").type(classPool.getCtClass(String.class.getName())).addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").withAnnotation(Include.class.getName()).addToClassPool(classPool);
				CtFieldBuilder.create().name("age").type(classPool.getCtClass(String.class.getName())).withAnnotation(Include.class.getName()).addToClass(ctClass1);
				CtMethodBuilder.create().publicAccess().returnType(classPool.getCtClass(String.class.getName())).name("getName").withAnnotation(Exclude.class.getName()).addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
		JApiClass jApiClass = getJApiClass(jApiClasses, "big.bang.theory.Sheldon");
		getJApiField(jApiClass.getFields(), "age");
		MatcherAssert.assertThat(jApiClass.getMethods().size(), is(0));
	}
}
