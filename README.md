# Progression 

Progression is a Clojure library designed to facilitate development with respect
to state changes. Progression doesn't manage state changes but, helps you
determine how state changed. Progression's goal is to make defining those
transitions an easier task with reusable facilities.

## Usage
Progression currently has one simple function called ```(transition ...)```.
This function returns non-nil values when both predicate functions are true for
the output of the pred transition function.

Take the following transition (deft is just (def ... (transition ...))):
```clojure
(deft some-transition :enabled? true? false? :user-id)
;;; or
(def some-transition (transition :enabled? true? false? :user-id))

```

The first argument, ```:enabled?``` is the predicate transition function. This
argument is a function that changes the old state and new state prior to
checking them against the predicate function. If this argument is nil, the input
doesn't change.

The second argument, ```true?``` is a Clojure function, in particular
a predicate that returns true the value being passed to it is true. This
predicate gets checked against the original state after it gets processed by the
predicate transition function. If this argument is nil, the prior value being
checked is only required to be distinct from the new value.

The third argument, ```false?``` is also a Clojure function that returns false
when the input is false. This predicate gets checked on the new value after
getting transformed by the predicate transition function. Like the last
argument, if this one is nil, the only requirement on the new value is that it's
distinct from the first.

An optional fourth argument, in this case ```:user-id``` is the function to be
applied to the new value if and only if both predicate functions are satisfied
and if the two values are distinct. If this value is not provided or is nil, the
transition function returns true when the conditions for a transition are met.

So running the following code:
```clojure
(some-transition {:user-id 1 :enabled? true} {:user-id 1 :enabled? false})
```
Would result in the value ```1``` being returned and:
```clojure
(some-transition {:user-id 1 :enabled? true} {:user-id 1 :enabled? true})
```
Would result in nil, as would:
```clojure
(some-transition {:user-id 1 :enabled? false} {:user-id 1 :enabled? false})
```

However, the following would return in a ```true``` value:
```clojure
(deft some-transition :enabled? true? false?)
(some-transition {:user-id 1 :enabled? true} {:user-id 1 :enabled? false})
```

...and this would return the entire map for the new value:
```clojure
(deft some-transition :enabled? true? false?)
(some-transition
  {:user-id 1 :enabled? true}
  {:user-id 1 :enabled? false}
  identity)
```

Please note: Both the old and new values before and after the predicate
transformation function must be distinct for any non-nil value to be returned.
So a transition is defined minimally as "two different values".

## License

Copyright © 2015 Jon Doane

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
