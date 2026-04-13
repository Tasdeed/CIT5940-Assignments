Part 4: 
1. It's purpose is to update our shortest path when a new path is found with less distance. If we didn't update, we would never improve our estimates. 
2. It requires a min heap because these shortest estimates are more likely to lead to a shortest path. If we used a regular queue, it would not be as efficient and could possibly lead to not finding the shortest path (but a valid path). 
3. I used PriorityQueue in Java and we were able to pass in a custom comparator. In Python, the heapq data structure has no comparator and Python compares element by element naturally. 

General Question: 
1. It took about 12 hours. 
2. Implementing the astar algo and changing my loading csv data to fit the algo. 
3. I used stackOverflow like websites (loading/ parsing CSV files) and ChatGPT (Java -> Python syntax). 
4. I learned how to use heaps in Python as I had never done that before. 