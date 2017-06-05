# Welcome

Welcome to **React4J**. This library aim to introduce Functional Reactive Programming in Java programming language.

It works with **Java 6, 7, and 8** versions. It have a total support or the **Java's 8 Lambdas** feature and can be also used with previous java version using the **anonymous inner classes**. React4J can also be embeded in **Android** project, just by adding the React4J.jar to it.

# Purpose

Functional Reactive Programming (FRP) is a high-level functional architecture that aims to work with interactions and time. It provides control flow structures for time. FRP consider states as time-varying values !

## Explanations
This is an example in classical imperative programming :
```java
Integer a = 0;
Integer b = 0;
Integer sum = a + b;

System.out.println("Sum = " + sum); // Sum = 0
a = 7;
b = 8;
System.out.println("Sum = " + sum); // Sum = 0
```
Zero result does not seems to be very surprising! Are you really sure of that ?

Yet, ask a non-programmer what he thinks about your code. He will tell you "hey dude! You don't know how to calculate ! 7 plus 8 gives 15 !"
You will probably answer something like that : "Yes !..... Ok !..... You have to recompute the sum and it is ok !"

Reactive Programming try to give a simpler answer to this problem by considering time varying states.
Imagine a language where you can write :

```java
Var a = 0;
Var b = 0;
Var sum = a + b;

print("Sum = " + sum); // Sum = 0
a = 7;
b = 8;
print("Sum = " + sum); // Sum = 15
```
Now, when **a** or **b** operand have changed, the **sum** result change too !
That's why **React4J** try to add this time varying states paradigm to Java.

This is what our previous non-Java program gives with Java + React4J

```java
final Var<Integer> a = new Var<>(0);
final Var<Integer> b = new Var<>(0);
Operation<Integer> sum = Operation.mergeOperation(() -> a.getValue() + b.getValue(), a, b); // reactive sum = reactive a + reactive b

System.out.println("Sum = " + sum.getValue()); // Sum = 0

a.setValue(7);
b.setValue(8);

System.out.println("Sum = " + sum.getValue()); // Sum = 15
```

If you do not have Java 8, find the Java 6 code source [here](https://github.com/YannCaron/React4J/blob/f742289393823282ebc4c1d8e55ff8c107268f4a/test/fr/cyann/react/VarOperationTest.java#L65) !

In this example **a**, **b** and **sum** are some state centered reacts. If **a** and **b** are changed **sum** value is automatically updated.
The sum of the operands **a** and **b** is always verifiable, even if any of its operands have changed after its statement.

## Notifications

Another really cool stuff with **React4J** is that you can be alerted when a state or a compount state have changed.
```java
final Var<Integer> a = new Var<>(0);
final Var<Integer> b = new Var<>(0);
Operation<Integer> sum = Operation.mergeOperation(() -> a.getValue() + b.getValue(), a, b); // reactive sum = reactive a + reactive b

sum.subscribe( (Integer sumValue) -> {
	System.out.println("Sum = " + sumValue);
})

a.setValue(7); // Sum = 7
b.setValue(8); // Sum = 15
```

## Var and Signals

Now imagine your computer is totally reactive !
What data can changed in a computer ? Mouse pointer, key, click, clock and so much more !

In the following example, we want to display the mouse position in human readable text :

```java
final Var<Integer> x = MouseReact.positionX().toVar(0); // mouse x reactive position
final Var<Integer> y = MouseReact.positionY().toVar(0); // mouse y reactive position

Operation<String> text = Operation.mergeOperation(() -> "mouse position (" + x.getValue() + ", " + y.getValue() + ")", x, y); // react text = "mouse position (" react x ", " react y ")"

text.subscribe((String textPosition) -> {
	System.out.println(textPosition);
});
```

That's it. You do not needs to manage the mouse event and attach to it some listeners.
You do not need to recalculate the string concatening, React4J do it for you !

The previous example show of to bring a device signal and compute it in a string Variable. But what the difference between Var and Signal ?

## Variables

In React4J, Var are time varying states. They have a continued state in time (and so a value a t0, e.g. an initial value). That's why we declared it `Var<Integer> a = new Var<>(0)`.
State or Var can be read every time in program with a getValue method.

![FRP - var](https://github.com/YannCaron/React4J/wiki/React4J%20-%20variable.png)

## Signals

Signals does not have continued value. A value is just transmeted when the event is emmited.

![FRP - var](https://github.com/YannCaron/React4J/wiki/React4J%20-%20signal.png)

A mecanism in reactive programming framework give the possibility to obtain Variable from signal easily.
We just need to specify the initial state of the var `Var<Integer> x = MouseReact.positionX().toVar(0)` and React4J is in charge of maintaint the value up to date according the signal emissions.

# Java Swing

Functional reactive programming finds its best application domain in user interactions like HMI, Web and video game developpment.
This is how to adapt a swing JLabel to feet with React4J and become a reactive Label :
```java
public class RLabel extends JLabel {

	@override
	public void setText(Var<String> text) {
		text.subscribe(value -> { setText(value); }); // each time text react is changed, the label text is updated
		setText(text.getValue()); // initialize value
	}

}
```
We overrided the setText method of JLabel to accept a reactive variable.

**Of course this strategy can be applied to any framework you want like gwt, javaFX or some object oriented game engines.**

Now we will rewrite our previous example, but in Functional way this time (we can chaine methods calling together like map / filter / fold as in java 8 lists).

```java
Var<String> mouseAndTime = MouseReact.button1()
		.map(arg1 -> arg1 ? "button pressed" : "button released")
		.toVar("no button yet !")
		.merge(MouseReact.positionX().toVar(0), (arg1, arg2) -> arg1 + " ( x=" + arg2)
		.merge(MouseReact.positionY().toVar(0), (arg1, arg2) -> arg1 + ", y=" + arg2 + ")");

label1.setText(mouseAndTime);
```
That's it, with a small handful of lines of code ! In fact, only one line, remember, method call are chained !

**Important** : If you do not have Java 8, find the Java 6 code source [here](https://github.com/YannCaron/React4J/blob/f742289393823282ebc4c1d8e55ff8c107268f4a/demo/fr/cyann/reactdemo/ReactDemo1.java#L68) !

We can dilute this source code to understand :
```java
Signal<Boolean> button1 = MouseReact.button1(); // emit each time user click on mouse button 1
Signal<String> btnStr1 = button1.map(btn -> btn ? "button pressed" : "button released"); // "pressed" each time button1 is true otherwise "released"
Var<String> btnStrVar1 = btnStr1.toVar("no button yet !"); // create a variable with default initial value (before user has clicked on button)

Var<Integer> posX = MouseReact.positionX().toVar(0); // emit each time user move mouse
Var<String> posStrX = posX.map(x -> "x=" + x);

Var<Integer> posY = MouseReact.positionY().toVar(0); // emit each time user move mouse
Var<String> posStrY = posY.map(y -> "y=" + y).toVar("y=NaN");

Var<String> posXY = posStrX.merge("", "", posStrY, (px, py) -> "(" + px + ", " + py + ")");
Var<String> allTogether = btnStrVar1.merge(posXY, (btn, pos) -> btn + " " + pos); // emited each time mouse is clicked or mouved and create resulting string

label1.setText(allTogether); // allTogether dump emitions to the label text
```

This is our reactive RLabel in action (updated on each mouse event; button or movement) :

![RLabel and merged mouse react](https://github.com/YannCaron/React4J/wiki/Demo1.png)

# Combination framework #

As **React4J** is based on functional monadic idea (the source of combination frameworks) it inherit their combination capabilities. We have seen it is possible to chaine map / filter and fold. We also be able to combine signals together with differents combination behaviours. In fact, React4J is a **domain specific language** dedicated to time events and value varying.

This is an illustration of differents combinations methods available in **React4J** :

![functions of React4J](https://github.com/YannCaron/React4J/wiki/React4J%20-%20functions.png)

Legend :

* **S** is a Signal (or a Var)
* **Sf** is a Signal Function (a functor)
* Different color means the type has changed
* Vertical bar is a synchronization
* Horizontal bar is garbaged signal

Explainations :

For High order functions -

* **filter** : The signal S1 is emited to the new signal S2 according the signal function Sf1 return true. Else it is garbaged.
* **map** : The value of the signal is transformed (in value or in type) by the signal function Sf1 and given to the signal S2.
* **fold** : The actual signal value and the previous are combined by the signal function Sf1 that give relative value to the new Signal S2.
* **filterFold** : The actual signal value and the previous are compared to determin if the value is emited to the new signal S2.

For Combination -

An interesting fact with reactive programming is the ability to combine signals together to obtaine new signal.

* **mergeSame** : Merge signal emission, when one signal emit the value is relayed to the S2 signal.
* **merge** : Merge signal emission, when one signal emit, a combination of the two signals (the emited one and the last emited by the other signal) is emited. The Sf1 function is in charge to "fold" them to a resulting signal.
* **sync** : Like merge, but the both signals should have emit before the resulting value is emited.
* **when** : Signal emit S1 value when S2 and S1 have sequentially emited.
* **when** : Signal emit S2 value when S1 and S2 have sequentially emited.
* **switchMap** : According the S1 signal value, Sf1 create a new Signal or null otherwise.
* **edge** : Like a transistor, S2 emit value only if S1 has emited true.
* **feedbackLoop** : When S1 emit, Sf1 is called that must create a new S2 signal. The S1 sinal is only emited when S1 and S2 emit together. (usefull to create smoothed signal)

## Demo ##
FRP will bring a great innovation in the **GUI frameworks** and web clients programming as well as in the field of **video games** development ! **I am actually minding about robotic applications of FRP to describe the Robot behaviours in case of its sensors events.**

Try react4j animation demo : [Download demo jar](https://github.com/YannCaron/React4J/wiki/React4J.jar)

Launch it with : `java -jar React4J.jar`

This demo demonstrate the react4j capabilities in the field game developpment. Every sprite is driven by reactions and has its own reactions like exit of display and colisions.
The concern of the programer become to describe what to do in case of some reactions. Everything else is managed by **React4J**

![Live demo](https://github.com/YannCaron/React4J/wiki/Demo3.png)

Youtube : [Youtube live demo](https://www.youtube.com/watch?v=0VGU9qcjnbM)

Have fun with reactive programming !

## How does it works ##
**React4J** fits in the discrete reactive frameworks familly e.g. it is event based.

There is two way to design an FRP framework :

* by **Continuous** reactions, a centralized manager maintains all reactives signals that are periodically actualized. It is an old and cpu consumer way because unnecessary values are computed in a sample time.

* by **Discrete** reactions that are event driver. The values are only updates if an event that change their value has been emiter.

React4J is based on this second option. When a method like filter is called from a reactive variable, a new signal is created. Then the old signal emit a value, the value is sent to the new signal through the signal function.

## Credits

**React4J** was inspired by Scala.react library currently developed by [Ingo Maier](https://github.com/ingoem) and [Martin Odersky](http://fr.wikipedia.org/wiki/Martin_Odersky) see https://github.com/ingoem/scala-react.
Some behaviors have been patterned after Haskell and ELM functional reactive programming capabilities : [FRP in Haskell](http://www.haskell.org/haskellwiki/Functional_Reactive_Programming) and [ELM programming language](http://elm-lang.org/learn/What-is-FRP.elm)

For more details about reactive programming and its benefits in comparison to Observer design pattern, see [Deprecating the Observer Pattern](http://infoscience.epfl.ch/record/176887/files/DeprecatingObservers2012.pdf) with **Scala.React**
