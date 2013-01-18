// Copyright (C) 2012 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.gerrit.server.account;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gerrit.reviewdb.client.AccountGroup;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.Collections;
import java.util.Queue;
import java.util.Set;

/**
 * Creates a GroupMembership checker for the internal group system, which
 * starts with the seed groups and includes all child groups.
 */
public class IncludingGroupMembership implements GroupMembership {
  public interface Factory {
    IncludingGroupMembership create(Iterable<AccountGroup.UUID> groupIds);
  }

  private final GroupIncludeCache groupIncludeCache;
  private final Set<AccountGroup.UUID> includes;
  private final Queue<AccountGroup.UUID> groupQueue;

  @Inject
  IncludingGroupMembership(
      GroupIncludeCache groupIncludeCache,
      @Assisted Iterable<AccountGroup.UUID> seedGroups) {
    this.groupIncludeCache = groupIncludeCache;
    this.includes = Sets.newHashSet(seedGroups);
    this.groupQueue = Lists.newLinkedList(seedGroups);
  }

  @Override
  public boolean contains(AccountGroup.UUID id) {
    if (id == null) {
      return false;
    }
    if (includes.contains(id)) {
      return true;
    }
    return findIncludedGroup(Collections.singleton(id));
  }

  @Override
  public boolean containsAnyOf(Iterable<AccountGroup.UUID> ids) {
    Set<AccountGroup.UUID> query = Sets.newHashSet();
    for (AccountGroup.UUID groupId : ids) {
      if (includes.contains(groupId)) {
        return true;
      }
      query.add(groupId);
    }

    return findIncludedGroup(query);
  }

  @Override
  public Set<AccountGroup.UUID> intersection(Iterable<AccountGroup.UUID> groupIds) {
    Set<AccountGroup.UUID> r = Sets.newHashSet();
    for (AccountGroup.UUID id : groupIds) {
      if (contains(id)) {
        r.add(id);
      }
    }
    return r;
  }

  private boolean findIncludedGroup(Set<AccountGroup.UUID> query) {
    boolean found = false;
    while (!found && !groupQueue.isEmpty()) {
      AccountGroup.UUID id = groupQueue.remove();

      for (final AccountGroup.UUID groupId : groupIncludeCache.getByInclude(id)) {
        if (includes.add(groupId)) {
          groupQueue.add(groupId);
          found |= query.contains(groupId);
        }
      }
    }

    return found;
  }

  @Override
  public Set<AccountGroup.UUID> getKnownGroups() {
    findIncludedGroup(Collections.<AccountGroup.UUID>emptySet()); // find all
    return Sets.newHashSet(includes);
  }
}
