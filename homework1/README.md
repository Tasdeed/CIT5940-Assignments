## Please enter your personal info here:
Name: Tasdid Hossain

PennKey (e.g., taliem): thossain 40621898


# Part 1:
## Are Alicia and Lloyd both wrong, or perhaps both right? Is only one of them correct? Why?
Lloyd is right because (assuming ArrayList) getting size and an index is O(1) which makes the first overall snippet constant time. The second snippet removes an element which is O(n) meanwhile the indexed elements are constant times which makes the overall second snippet O(n).


# Part 2:
## What are the Big O and Big Ω times for snippets C and D?
Snippet C: Big O = O(n^2) if target is at end or not in matrix, Big Omega = O(1) if target is first in the matrix
Snippet D: It's always O(n^2) because there's no early checks, it's going to go through the whole matrix every time

## When measuring actual runtime, does one of the snippets run faster than the other? In what situations? Why do you think this is the case?
Snippet C runs faster because it can early exit once finding the value, D will always run the whole matrix.


## What else do you notice about the reported runtime? Is it 100% consistent every time you run it?
It's not consistent every time for both snippets. I would assume this depends on both input size and the target location (for snippet C because it can early exit). It also depends on the processes running and the computer timings because not every run is the exact same. 


# Part 3:
## Before you make any changes, explain whether you think a LinkedList or an ArrayList makes more sense in this instance. Which do you think will be faster? Why?
I would say a LinkedList would make more sense and be faster in this instance. The IT tickets need to be both added and removed quickly. An arrayList would make lookup and adding to the end quicker but for tickets they are usually completed in the order they are received so lookup would usually not be needed. We would also need to remove the earlier elements in order which is much faster in a LinkedList and would make accessing the first element faster anyway.


## When measuring actual runtime, is the LinkedList version Suho wrote, or your ArrayList version faster? Does this change when the list size is small versus when it is very large?
For short queues: both versions ran in 2-3ms. 
For long queues: Arraylist: 199-283ms Average: 211ms
                LinkedList: 205-300ms Average: 220ms
For short queues, they are basically the same speed. At longer queues, the ArrayList version runs a bit faster but not always.


## If you ignore queue creation times, does that affect which ticket processor version is faster?
No, because they are both O(n) time to add elements. 


## Write a paragraph or two in the style of a technical report (think about – how would I write this professionally if I needed to explain my findings to my manager?).
Your report should answer the following questions:
* What did you learn from this experience?
* Which implementation do you suggest should be used? Are there certain situations that might call for the other approach?
* How does the theoretical time complexity compare with your findings?

We have learned that the ArrayList implementation for a ticket processor is better for scaled operations and long term usage. At small queues, there is no difference in runtime but for workflows we would want to use ArrayList due to the possibility of being backed up. Theoretically, the two implementations should be the same runtime but in actuality, there is a slight edge to the ArrayList implementation. This is possibly due to CPU or cache efficiency. 

# Part 4
## What are the Big O and Big Ω times for Javier's algorithm? What are the Big O and Big Ω for space use?
The big O and omega times are both O(n log n) because we have to go through both arrays no matter what, but they are split into smaller pieces. The spaces are also O(n) for both because we have to create a new array for merging them together.


## Write a paragraph or two in the style of a technical report (think about – how would I write this professionally if I needed to explain my findings to my manager?). 
Your report should answer the following questions:
* Which of the two algorithms (yours versus Javier's) is more efficient in time and space (in terms of Big O)
    * What about in actual runtime?
* Which implementation do you suggest should be used? Are there certain situations that might call for the other approach?

The recursive and iterative implementations both have the same time complexity of O(n log n) because we are still going through every element, dividing the whole array into smaller portions. We are using more space in the recrusive implementation (n log n) than the iterative (n) because we are creating a new array in every recursive call. I believe in actual runtime, the iterative implementation would be faster due to having less objects on the stack. 
I suggest to we use the iterative approach to save on space resources. However, the recursive approach could be used to have better readability and possibly scale faster because it's easier to implement for this sorting algorithm. 

