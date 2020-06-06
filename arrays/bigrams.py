import sys, string
import numpy as np
from collections import Counter
from functools import reduce

leet = {'A': '4', 'B': '8', 'C':'<','E': '3', 'G': '6', 'H':'#','I': '1', 
 'O': '0', 'S': '5', 'T': '7', 'Y':' ¥','Z': '2'}

# Read File Array Chars
characters = np.array([' ']+list(open(sys.argv[1]).read())+[' '])

# Normalize
characters[~np.char.isalpha(characters)] = ' '
characters = np.char.upper(characters)

# Filter Leet Characters
characters =  np.frompyfunc(lambda s: s.translate(str.maketrans(leet)),1,1)(characters)

### Split the words by finding the indices of spaces
sp = np.where(characters == ' ')

sp2 = np.repeat(sp, 2)

# Get the pairs as a 2D matrix, skip the first and the last
w_ranges = np.reshape(sp2[1:-1], (-1, 2))

# Remove the indexing to the spaces themselves
w_ranges = w_ranges[np.where(w_ranges[:, 1] - w_ranges[:, 0] > 2)]

# Voila! Words are in between spaces, given as pairs of indices
words = list(map(lambda r: characters[r[0]:r[1]], w_ranges))

# Let's recode the characters as strings
swords = np.array(list(map(lambda w: ''.join(w).strip(), words)))

# Find Two Grams of Non Stop Words
bigrams = Counter(zip(swords, swords[1:]))

# # Print Top 5 Results
for w, c in bigrams.most_common(5):
    print(w[0], w[1], '-', c)

# list(map(lambda x:print(x[0],"-",x[1]), bigrams.most_common(5)))


print(list(filter(lambda x:print( x[0],x[1]," - "), bigrams.most_common(5))))

new_list = list(map(lambda x:print( x[0],x[1]), bigrams.most_common(5)))

print(new_list)