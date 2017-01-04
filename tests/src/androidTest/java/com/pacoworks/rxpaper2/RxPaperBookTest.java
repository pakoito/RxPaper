
/*
 * The MIT License (MIT)
 * Copyright (c) 2016 pakoito & 2015 César Ferreira
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit  persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.pacoworks.rxpaper2;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.pacoworks.rxpaper2.sample.MainActivity;
import com.pacoworks.rxpaper2.sample.model.ComplexObject;
import com.pacoworks.rxpaper2.sample.model.ImmutableObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

@RunWith(AndroidJUnit4.class)
public class RxPaperBookTest {

    @Rule
    public final ActivityTestRule<MainActivity> activity = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        RxPaperBook.init(activity.getActivity().getApplicationContext());
        RxPaperBook.with("WRITE").destroy().subscribe();
        RxPaperBook.with("WRITE_ERROR").destroy().subscribe();
        RxPaperBook.with("READ").destroy().subscribe();
        RxPaperBook.with("READ_WITH_DEFAULT").destroy().subscribe();
        RxPaperBook.with("DELETE").destroy().subscribe();
        RxPaperBook.with("EXISTS").destroy().subscribe();
        RxPaperBook.with("KEYS").destroy().subscribe();
        RxPaperBook.with("DESTROY").destroy().subscribe();
        RxPaperBook.with("UPDATES_UNCH").destroy().subscribe();
        RxPaperBook.with("UPDATES_CH").destroy().subscribe();
    }

    @Test
    public void testWrite() throws Exception {
        RxPaperBook book = RxPaperBook.with("WRITE", Schedulers.trampoline());
        final String key = "hello";
        final Completable write = book.write(key, ComplexObject.random());
        Assert.assertFalse(book.book.exist(key));
        final TestObserver<Object> testSubscriber = TestObserver.create();
        write.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
        Assert.assertTrue(book.book.exist(key));
    }

    @Test
    public void testReadError() throws Exception {
    }

    @Test
    public void testRead() throws Exception {
        RxPaperBook book = RxPaperBook.with("READ", Schedulers.trampoline());
        final String key = "hello";
        final ComplexObject value = ComplexObject.random();
        book.write(key, value).subscribe();
        final TestObserver<ComplexObject> testSubscriber = TestObserver.create();
        book.<ComplexObject>read(key).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertValues(value);
        // notFoundSubscriber
        final TestObserver<ComplexObject> notFoundSubscriber = TestObserver.create();
        String noKey = ":(";
        book.<ComplexObject>read(noKey).subscribe(notFoundSubscriber);
        notFoundSubscriber.awaitTerminalEvent();
        notFoundSubscriber.assertError(IllegalArgumentException.class);
        // incorrectTypeSubscriber
        book.<Integer>read(key).subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Integer value) {
                Assert.fail();
            }

            @Override
            public void onError(Throwable e) {
                if (!(e instanceof ClassCastException)) {
                    Assert.fail(e.getMessage());
                }
            }
        });
        // immutable objects
        book.write(key, new ImmutableObject(key)).subscribe();
        final TestObserver<ImmutableObject> immutableReadSubscriber = TestObserver.create();
        book.<ImmutableObject>read(key).subscribe(immutableReadSubscriber);
        immutableReadSubscriber.awaitTerminalEvent();
        immutableReadSubscriber.assertNoErrors();
        immutableReadSubscriber.assertComplete();
        Assert.assertNotEquals(null, immutableReadSubscriber.getEvents().get(0));
    }

    @Test
    public void testReadWithDefault() throws Exception {
        RxPaperBook book = RxPaperBook.with("READ_WITH_DEFAULT", Schedulers.trampoline());
        final String key = "hello";
        final ComplexObject value = ComplexObject.random();
        book.write(key, value).subscribe();
        final TestObserver<ComplexObject> testSubscriber = TestObserver.create();
        book.<ComplexObject>read(key).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertValues(value);
        // notFoundSubscriber
        final TestObserver<ComplexObject> notFoundSubscriber = TestObserver.create();
        String noKey = ":(";
        final ComplexObject defaultValue = ComplexObject.random();
        book.read(noKey, defaultValue).subscribe(notFoundSubscriber);
        notFoundSubscriber.awaitTerminalEvent();
        notFoundSubscriber.assertNoErrors();
        notFoundSubscriber.assertValueCount(1);
        notFoundSubscriber.assertValues(defaultValue);
        // incorrectTypeSubscriber
        book.<Integer>read(key).subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Integer value) {
                Assert.fail("Expected ClassCastException");
            }

            @Override
            public void onError(Throwable e) {
                if (!(e instanceof ClassCastException)) {
                    Assert.fail(e.getMessage());
                }
            }
        });
    }

    @Test
    public void testDelete() throws Exception {
        RxPaperBook book = RxPaperBook.with("DELETE", Schedulers.trampoline());
        final String key = "hello";
        final TestObserver<ComplexObject> errorSubscriber = TestObserver.create();
        book.delete(key).subscribe(errorSubscriber);
        errorSubscriber.awaitTerminalEvent();
        errorSubscriber.assertComplete();
        errorSubscriber.assertNoErrors();
        book.write(key, ComplexObject.random()).subscribe();
        final TestObserver<ComplexObject> testSubscriber = TestObserver.create();
        book.<ComplexObject>delete(key).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertComplete();
        testSubscriber.assertNoErrors();
        Assert.assertFalse(book.book.exist(key));
    }

    @Test
    public void testExists() throws Exception {
        RxPaperBook book = RxPaperBook.with("EXISTS", Schedulers.trampoline());
        final String key = "hello";
        book.write(key, ComplexObject.random()).subscribe();
        final TestObserver<Boolean> foundSubscriber = TestObserver.create();
        book.exists(key).subscribe(foundSubscriber);
        foundSubscriber.awaitTerminalEvent();
        foundSubscriber.assertNoErrors();
        foundSubscriber.assertValueCount(1);
        foundSubscriber.assertValues(true);
        // notFoundSubscriber
        final TestObserver<Boolean> notFoundSubscriber = TestObserver.create();
        String noKey = ":(";
        book.exists(noKey).subscribe(notFoundSubscriber);
        notFoundSubscriber.awaitTerminalEvent();
        notFoundSubscriber.assertComplete();
        notFoundSubscriber.assertValueCount(1);
        notFoundSubscriber.assertValues(false);
    }

    @Test
    public void testGetAllKeys() throws Exception {
        RxPaperBook book = RxPaperBook.with("KEYS", Schedulers.trampoline());
        final String key = "hello";
        final String key2 = "you";
        final ComplexObject value = ComplexObject.random();
        book.write(key, value).subscribe();
        book.write(key2, value).subscribe();
        final TestObserver<List<String>> foundSubscriber = TestObserver.create();
        book.keys().subscribe(foundSubscriber);
        foundSubscriber.awaitTerminalEvent();
        foundSubscriber.assertNoErrors();
        foundSubscriber.assertValueCount(1);

        foundSubscriber.assertValue(book.book.getAllKeys());
    }

    @Test
    public void testDestroy() throws Exception {
        RxPaperBook book = RxPaperBook.with("DESTROY", Schedulers.trampoline());
        final String key = "hello";
        final String key2 = "you";
        final ComplexObject value = ComplexObject.random();
        book.write(key, value).subscribe();
        book.write(key2, value).subscribe();
        final TestObserver<Void> destroySubscriber = TestObserver.create();
        book.destroy().subscribe(destroySubscriber);
        destroySubscriber.awaitTerminalEvent();
        destroySubscriber.assertComplete();
        destroySubscriber.assertNoErrors();
        destroySubscriber.assertValueCount(0);
        Assert.assertFalse(book.book.exist(key));
        Assert.assertFalse(book.book.exist(key2));
    }

    @Test
    public void testUpdatesUnchecked() throws Exception {
        RxPaperBook book = RxPaperBook.with("UPDATES_UNCH", Schedulers.trampoline());
        final String key = "hello";
        final ComplexObject value = ComplexObject.random();
        final TestObserver<ComplexObject> updatesSubscriber = TestObserver.create();
        book.<ComplexObject>observeUnsafe(key).subscribe(updatesSubscriber);
        updatesSubscriber.assertValueCount(0);
        book.write(key, value).subscribe();
        updatesSubscriber.assertValueCount(1);
        updatesSubscriber.assertValue(value);
        final ComplexObject newValue = ComplexObject.random();
        book.write(key, newValue).subscribe();
        updatesSubscriber.assertValueCount(2);
        updatesSubscriber.assertValues(value, newValue);
        // Error value
        final int wrongValue = 3;

        //Test observer removes type safety from values, so class cast doesn't throws
        book.<ComplexObject>observeUnsafe(key).subscribe(new DefaultObserver<ComplexObject>() {
            @Override
            public void onNext(ComplexObject value) {
                Assert.fail("Expected ClassCastException while writing");
            }

            @Override
            public void onError(Throwable e) {
                Assert.fail("Expected ClassCastException while writing");
            }

            @Override
            public void onComplete() {
                Assert.fail("Expected ClassCastException while writing");
            }
        });

        TestObserver<Object> failOnWriteObserver = new TestObserver<>();
        book.write(key, wrongValue).subscribe(failOnWriteObserver);
        failOnWriteObserver.assertError(ClassCastException.class);
        failOnWriteObserver.assertNoValues();
        failOnWriteObserver.assertNotComplete();
    }

    @Test
    public void testUpdatesChecked() throws Exception {
        RxPaperBook book = RxPaperBook.with("UPDATES_CH", Schedulers.trampoline());
        final String key = "hello";
        final ComplexObject value = ComplexObject.random();
        final TestObserver<ComplexObject> updatesSubscriber = TestObserver.create();
        book.observe(key, ComplexObject.class).subscribe(updatesSubscriber);
        updatesSubscriber.assertValueCount(0);
        book.write(key, value).subscribe();
        updatesSubscriber.assertValueCount(1);
        updatesSubscriber.assertValues(value);
        final ComplexObject newValue = ComplexObject.random();
        book.write(key, newValue).subscribe();
        updatesSubscriber.assertValueCount(2);
        updatesSubscriber.assertValues(value, newValue);
        // Error value
        final int wrongValue = 3;
        book.write(key, wrongValue).subscribe();
        updatesSubscriber.assertValueCount(2);
        updatesSubscriber.assertValues(value, newValue);
        updatesSubscriber.assertNoErrors();
    }
}
