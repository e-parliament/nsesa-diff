/**
 * Copyright 2013 European Parliament
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 */
package org.nsesa.diff.diffUtils;


public final class WordBasedCleanup {

//	public static final void semanticCleanup(List<Word> original, List<Word> revised, Patch patch, int wordLength) {
//		// Just be sure that we're cleaning up something "dirty"
//		if (!patch.getDeltas().isEmpty()) {
//			Deque<Delta> stack = new ArrayDeque<Delta>(patch.getDeltas());
//			
//			while (stack.peek() != null) {
//				Delta current = stack.poll();
//				
//				if (stack.peek() != null) {
//					Delta next = stack.peek();
//					
//					if (current.getType() != TYPE.DELETE && next.getType() != TYPE.DELETE) {
//						int currentPosition = current.getRevised().getPosition() + current.getRevised().size() - 1;
//						int nextPosition = next.getRevised().getPosition();
//						
//						List<Word> revisedContainers = new ArrayList<Word>();						
//						for (int i = currentPosition + 1; i < nextPosition; i++) {
//							revisedContainers.add(revised.get(i));							
//						}
//						
//						currentPosition = current.getOriginal().getPosition() + current.getOriginal().size() - 1;
//						nextPosition = next.getOriginal().getPosition();
//						
//						List<Word> originalContainers = new ArrayList<Word>();						
//						for (int i = currentPosition + 1; i < nextPosition; i++) {
//							originalContainers.add(original.get(i));							
//						}
//						
//						StringBuilder assembled = new StringBuilder();
//						for (Word word : revisedContainers) {
//							assembled.append(word.getValue());
//						}
//						
//						if (evaluateValue(assembled.toString(), wordLength)) {
//							for (int i = 0; i < revisedContainers.size(); i++) {
//								revisedContainers.get(i).setChange();
//								originalContainers.get(i).setChange();
//							}
//						}									
//					} else if ((current.getType() == TYPE.DELETE && next.getType() != TYPE.INSERT) || (current.getType() == TYPE.CHANGE && next.getType() == TYPE.DELETE)) {
//						// Cleanup induced by original. DELETE can be followed by DELETE or CHANGE.
//						int currentPosition = current.getOriginal().getPosition() + current.getOriginal().size() - 1;
//						int nextPosition = next.getOriginal().getPosition();
//						
//						List<Word> revisedContainers = new ArrayList<Word>();						
//						for (int i = currentPosition + 1; i < nextPosition; i++) {
//							revisedContainers.add(original.get(i));							
//						}
//						
//						currentPosition = current.getRevised().getPosition() + current.getRevised().size() - 1;
//						nextPosition = next.getRevised().getPosition();
//						
//						List<Word> originalContainers = new ArrayList<Word>();						
//						for (int i = currentPosition + 1; i < nextPosition; i++) {
//							originalContainers.add(revised.get(i));							
//						}
//						
//						StringBuilder assembled = new StringBuilder();
//						for (Word word : revisedContainers) {
//							assembled.append(word.getValue());
//						}
//						
//						if (evaluateValue(assembled.toString(), wordLength)) {
//							for (int i = 0; i < revisedContainers.size(); i++) {
//								revisedContainers.get(i).setChange();
//								originalContainers.get(i).setChange();
//							}
//						}							
//					} 
//				}			
//			}
//		}
//	}

    private static boolean evaluateValue(String text, int wordLength) {
        // if text contains the end punctuation of a couple
        if (text.matches("(\"|\\)|>|}|\\]).*")) {
            return false;
        }

        String[] splitOnPunctuation = text.trim().split("\\p{P}");
        if (splitOnPunctuation.length == 2) {
            return evaluateValue(splitOnPunctuation[0].trim(), wordLength) && evaluateValue(splitOnPunctuation[1].trim(), wordLength);
        } else {
//			if (text.trim().split("\\s").length <= 2 && text.trim().length() <= wordLength && !text.trim().matches("\\p{P}")) {
//				return true;
//			}
            if (text.trim().split("\\s").length <= 2 && text.trim().length() <= wordLength && !text.matches("\\p{P}")) {
                return true;
            }
        }

        return false;
    }

}
