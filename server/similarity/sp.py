from similarity.ed import EditDistance
from similarity.ds import DempsterShafer
from similarity.models import Similarity
from authenticate.models import User
from datetime import date
import time, math

class SimilarityProcess():
    def processUser(self, userId):
        user1 = User.objects.select_related().get(userId=userId)

        similarityValue = {}
        response = ""
        for user2 in User.objects.select_related().exclude(userId=userId):
            simValue = self.process(user1, user2)
            
            # response += user1.userName + " & " + user2.userName + " simValue : " + str(simValue) + "<br />" 
            similarityValue[user2.userId] = simValue
            
            # ''' update data on db
            sim = Similarity.objects.get(
                Q(similarityUser1_id=user1.userId) & Q(similarityUser2_id=user2.userId) | 
                Q(similarityUser1_id=user2.userId) & Q(similarityUser2_id=user1.userId)  
            )
            sim.similarityValue = simValue
            sim.save()
            # '''
        
    def process(self, user1, user2, type):
        response = ""
        weightAttribute = {
                           1 : {'userFoods' : 0.25, 'userDrinks' : 0.25, 'userBooks' : 0.05, 'userMovies' : 0.05, 'userOccupation' : 0.1, 'userDOB' : 0.2, 'userGender' : 0.1},
                           2 : {'userFoods' : 0.175, 'userDrinks' : 0.125, 'userBooks' : 0.1, 'userMovies' : 0.1, 'userOccupation' : 0.175, 'userDOB' : 0.175, 'userGender' : 0.15},
                           }
        
        user1Property = {}
        user1Property['userFoods'] = user1.userFoods.lower().split(',')
        user1Property['userDrinks'] = user1.userDrinks.lower().split(',')
        user1Property['userBooks'] = user1.userBooks.lower().split(',')
        user1Property['userMovies'] = user1.userMovies.lower().split(',')
        user1Property['userOccupation'] = user1.userOccupation.lower()
        user1Property['userDOB'] = str(user1.userDOB).split('-')
        user1Property['userGender'] = user1.userGender.lower()
        
        user2Property = {}
        user2Property['userFoods'] = user2.userFoods.lower().split(',')
        user2Property['userDrinks'] = user2.userDrinks.lower().split(',')
        user2Property['userBooks'] = user2.userBooks.lower().split(',')
        user2Property['userMovies'] = user2.userMovies.lower().split(',')
        user2Property['userOccupation'] = user2.userOccupation.lower()
        user2Property['userDOB'] = str(user2.userDOB).split('-')
        user2Property['userGender'] = user2.userGender.lower()
        
        attributeValue = {}
        for keyProperty in user1Property:
            if keyProperty == 'userGender':
                average = 0.7 if user1Property['userGender'] == user2Property['userGender'] else 0.3
                method = "If-else"
            elif keyProperty == 'userDOB':
                delta = date(int(user1Property['userDOB'][0]), int(user1Property['userDOB'][1]), int(user1Property['userDOB'][2])) - date(int(user2Property['userDOB'][0]), int(user2Property['userDOB'][1]), int(user2Property['userDOB'][2]))
                # diff = float((float(delta.days) + 1825.0)/3650.0)
                diff = float((1825.0 - float(math.fabs(delta.days))) / 1825.0)
                average = 0.0 if diff < 0.0 else diff
                method = "Difference"
            elif keyProperty == 'userOccupation':
                ed = EditDistance(user1Property[keyProperty], user2Property[keyProperty])
                average = ed.similarity1()
                method = "EditDistance"
            else:
                sumOfItem = 0
                numOfItem = 0
                
                listTemp = []
                for p1 in user1Property[keyProperty]:
                    for p2 in user2Property[keyProperty]:
                        ed = EditDistance(p1, p2)
                        edValue = ed.similarity1()
                        
                        sumOfItem += edValue
                        numOfItem += 1
                        strTemp = keyProperty + " => [EditDistance]  " + p1 + " & " + p2 + " = " + str(edValue) + "<br />"
                        
                        #listTemp.append((edValue, strTemp))
                        
                # for l in sorted(listTemp, key=lambda attr: attr[0], reverse=True):
                #    response += l[1]
                        
                average = sumOfItem / numOfItem
                method = "Average EditDistance"
            
            newSim = average    
            #newSim = float((2 * average * weightAttribute[type][keyProperty]) / (1 + (average * weightAttribute[type][keyProperty])))
            # newSim = average * weightAttribute[type][keyProperty]
            attributeValue[keyProperty] = newSim
            
            response += keyProperty + " => " + method + " = " + str(newSim) + "<br />"
        
        # '''
        ds = DempsterShafer()
        simValue = ds.process2(attributeValue) #* 1000000
        # response += "DS : " + str(simValue) + "<br />"
        # '''
        # simValue = reduce(lambda x, y: x + y / float(len(attributeValue.values())), attributeValue.values(), 0)
        # simValue = sum(attributeValue.values(), 0.0) / len(attributeValue.values())
        # response += user1.userName + " & " + user2.userName + " simValue : " + str(simValue) + "<br />" 
        # response += str(time.clock() - start_time) + " seconds<br />"
        response += "<br/>"
        # simValue = response

        return simValue
        
