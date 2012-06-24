(ns clojurec.core-test
  (:use clojure.test
        clojurec.core))

(defn- run [x]
  (run-expr 'clojurec.core-test false x))

(defn- core-run [x]
  (run-expr 'clojurec.core-test true x))

(deftest basic
  (testing "very basic stuff"
    (is (= (run '(cljc.core/print nil)) [nil]))
    (is (= (run '(cljc.core/print (has-type? 1 Integer))) [true]))
    (is (= (run '(def heusler (fn* ([x] x)))) []))
    (is (= (run '(cljc.core/print (let [a 1 a a] a))) [1]))
    (is (= (run '(cljc.core/print (((fn* ([x] (fn* ([y] x)))) 1) 2))) [1]))
    (is (= (run '(do (cljc.core/print 1) (cljc.core/print 2))) [1 2]))))

(deftest protocols
  (testing "protocols"
    (is (= (run '(defprotocol* foo (-bar [x y]))) []))
    (is (= (run '(defprotocol foo (-bar [x y]))) []))
    (is (= (run '(let [x (fn [] 1)]
			(cljc.core/print (. x -invoke))))
	   [1]))))

(deftest types
  (testing "deftype"
    (is (= (run '(deftype* Cons [first rest])) []))
    (is (= (run '(do
		   (deftype* Cons [first rest])
		   (def f (fn [c] (c* "DEFTYPE_GET_FIELD (~{}, 0)" c)))
		   (def r (fn [c] (c* "DEFTYPE_GET_FIELD (~{}, 1)" c)))
		   (let [c (Cons 1 2)]
		     (cljc.core/print (f c))
		     (cljc.core/print (r c)))))
	   [1 2]))))

(deftest types-protocols
  (testing "types with protocols"
    (is (= (run '(do
		   (defprotocol ISeq
		     (-first [coll])
		     (-rest [coll]))
		   (deftype Cons [first rest]
		     ISeq
		     (-first [coll] first)
		     (-rest [coll] rest))
		   (let [c (Cons 1 2)]
		     (cljc.core/print (. c -first))
		     (cljc.core/print (. c -rest)))))
	   [1 2]))))

(deftest numbers
  (testing "simple numbers"
    (is (= (run '(cljc.core/print (+ 1 2))) [3]))
    (is (= (run '(cljc.core/print (< 1 2))) [true]))
    (is (= (run '(cljc.core/print (< 1 1))) [false]))
    (is (= (run '(cljc.core/print (< 2 1))) [false]))))

(deftest loops
  (testing "simple loops"
    (is (= (run '(loop [i 2
			x 1]
		   (if (< i 11)
		     (recur (+ i 1) (* x i))
		     (cljc.core/print x))))
	   [3628800]))))

(deftest core
  (testing "cljc.core"
    (is (= (core-run '(let [c (cljc.core/Cons 1 2)]
			(cljc.core/print (. c -first))
			(cljc.core/print (. c -rest))))
	   [1 2]))
    (is (= (core-run '(loop [l (cljc.core/Cons 1 (cljc.core/Cons 2 nil))]
			(when (cljc.core/seq l)
			  (do
			    (cljc.core/print (cljc.core/first l))
			    (recur (cljc.core/rest l))))))
	   [1 2]))
    (is (= (core-run '(loop [l '(1 2 3)]
			(when (cljc.core/seq l)
			  (do
			    (cljc.core/print (cljc.core/first l))
			    (recur (cljc.core/rest l))))))
	   [1 2 3]))))

(deftest functions
  (testing "functions"
    (is (= (core-run '(do
			(defn printer [a b c d e]
			  (cljc.core/print a)
			  (cljc.core/print b)
			  (cljc.core/print c)
			  (cljc.core/print d)
			  (cljc.core/print e))
			(printer 1 2 3 4 5)))
	   [1 2 3 4 5]))))

;;(run-tests *ns*)