(ns progression)

(defn tx-or
  [tx-fn or-fn & args]
  (apply (if (nil? tx-fn) or-fn tx-fn) args))

(defn tx-or-ident
  [tx-fn & args]
  (apply (partial tx-or tx-fn identity) args))

(defn tx-or-true
  [tx-fn & args] (apply (partial tx-or tx-fn (constantly true)) args))

(defn transition
  "Defines a transition.
  
  pred-tx-fn: This fn gets run on both the new and old values before getting
  passed on to the predicate fns. If nil, idenity is assumed.
  pre-pred-fn: A predicate that is checked against the transformed old value.
  post-pred-fn: A predicate that is checked against the transformed new value.
  post-tx-fn: A fn that takes in the old value then new value and spits out
  something else when the old and new values are not equal, their transitioned
  equivelents are not equal, and when both pred (when not nil) are true for the
  old and new values respectively. If this argument is left nil, true is
  returned to signify a transition has occured.
  
  All arguments may be nil, in which case this is simply checking to see if the
  two values are not equal, which is the basis for any transition. Also the
  three arity version of this function is simply the four arity version where
  post-pred-fn is nil (expect a true or nil as a result.)"
  ([pred-tx-fn pre-pred-fn post-pred-fn]
   (transition pred-tx-fn pre-pred-fn post-pred-fn nil))
  ([pred-tx-fn pre-pred-fn post-pred-fn post-tx-fn]
   (fn [old-value new-value]
     (let [[ov-tx nv-tx] (map #(tx-or-ident pred-tx-fn %) [old-value new-value])]
       (when (and (not= old-value new-value)
                  (not= ov-tx nv-tx)
                  (tx-or-true pre-pred-fn ov-tx)
                  (tx-or-true post-pred-fn nv-tx))
         (tx-or-true post-tx-fn new-value))))))

(defmacro deft
  "def(ine)t(transition). This macro just calls transition and defines the
  resulting function."
  ([def-name pred-tx-fn pre-pred-fn post-pred-fn]
  `(deft ~def-name ~pred-tx-fn
     ~pre-pred-fn ~post-pred-fn nil))
  ([def-name pred-tx-fn pre-pred-fn post-pred-fn post-tx-fn]
   `(def ~def-name
      (transition
        ~pred-tx-fn ~pre-pred-fn
        ~post-pred-fn ~post-tx-fn))))

(comment

  (deft foo :enabled? true? false? :user-id)
  (def foo (transition :enabled? true? false? :user-id))
  (foo {:user-id 1 :enabled? true} {:user-id 1 :enabled? false})

  )


